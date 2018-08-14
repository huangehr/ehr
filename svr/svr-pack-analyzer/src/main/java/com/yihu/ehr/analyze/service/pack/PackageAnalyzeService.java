package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.PackageMgrClient;
import com.yihu.ehr.analyze.model.ZipPackage;
import com.yihu.ehr.analyze.service.qc.PackageQcService;
import com.yihu.ehr.analyze.service.qc.StatusReportService;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.AnalyzeStatus;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.exception.*;
import com.yihu.ehr.profile.queue.RedisCollection;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 档案质控引擎.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Service
public class PackageAnalyzeService {

    private final static Logger logger = LoggerFactory.getLogger(PackageAnalyzeService.class);
    private static final String INDEX = "json_archives_qc";
    private static final String QC_DATASET_INFO = "qc_dataset_info";
    private static final String QC_METADATA_INFO = "qc_metadata_info";

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private PackageQcService packageQcService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private StatusReportService statusReportService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;


    /**
     * analyze 档案分析服务
     * 1.从队列中取出档案
     * 2.分析档案
     * 3.数据入库
     *
     * @author Airhead
     * @created 2018.01.15
     */
    public void analyze() {
        boolean main = true;
        Serializable serializable = redisTemplate.opsForList().rightPop(RedisCollection.AnalyzeQueue);
        if (null == serializable) {
            serializable = redisTemplate.opsForSet().pop(RedisCollection.AnalyzeQueueVice);
            main = false;
        }
        EsSimplePackage esSimplePackage = null;
        ZipPackage zipPackage = null;
        try {
            if (serializable != null) {
                String packStr = serializable.toString();
                esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            }
            if (esSimplePackage != null) {
                //判断是否已经 解析完成|| 正在解析 || 质控完成 || 正在质控  (由于部署多个服务,运行的时间差可能导致多次加入队列,造成多次质控,解析)
                Map<String, Object> map = statusReportService.getJsonArchiveById(esSimplePackage.get_id());
                if(map != null){
                    if("3".equals(map.get("analyze_status")+"") || "1".equals(map.get("analyze_status")+"")){//已经质控完成的,或者正在质控的  直接返回
                        logger.error("sssssssss====>>analyze_status:"+map.get("analyze_status"));
                        logger.error("id:"+map.get("_id"));
                        return;
                    }
                    //如果已经到了解析流程,必定已经完成质控流程
                    if("3".equals(map.get("archive_status")+"") || "1".equals(map.get("archive_status")+"")){ //如果已经解析完成了,或者正在解析, 将其标记为 已完成质控
                        logger.error("rrrrrrr====>>archive_status:"+map.get("archive_status"));
                        logger.error("id:"+map.get("_id"));
                        statusReportService.reportStatus(esSimplePackage.get_id(), AnalyzeStatus.Finished, 0, null);
                        return;
                    }
                }
                statusReportService.reportStatus(esSimplePackage.get_id(), AnalyzeStatus.Acquired, 0, "正在质控中");
                zipPackage = new ZipPackage(esSimplePackage);
                zipPackage.download();
                zipPackage.unZip();
                ProfileType profileType = zipPackage.resolve();
                if (ProfileType.Standard == profileType && !zipPackage.isReUploadFlg()) {
                    packageQcService.qcHandle(zipPackage);
                    //保存数据集质控数据
                    elasticSearchUtil.index(INDEX, QC_DATASET_INFO, zipPackage.getQcDataSetRecord());
                    //保存数据元质控数据
                    elasticSearchUtil.bulkIndex(INDEX, QC_METADATA_INFO, zipPackage.getQcMetadataRecords());
                    //报告质控状态
                    statusReportService.reportStatus(esSimplePackage.get_id(), AnalyzeStatus.Finished, 0, "Qc success");
                } else {
                    //报告非结构化档案包质控状态
                    statusReportService.reportStatus(esSimplePackage.get_id(), AnalyzeStatus.Finished, 0, "Ignore non-standard package file or re-upload package file");
                }
                //发送解析消息
                if (main) {
                    redisTemplate.opsForList().leftPush(RedisCollection.ResolveQueue, objectMapper.writeValueAsString(esSimplePackage));
                } else {
                    redisTemplate.opsForSet().add(RedisCollection.ResolveQueueVice, objectMapper.writeValueAsString(esSimplePackage));
                }
            }
        } catch (Throwable e) {
            int errorType = -1;
            if (e instanceof ZipException) {
                errorType = 1;
            } else if (e instanceof IllegalJsonFileException) {
                errorType = 2;
            } else if (e instanceof IllegalJsonDataException) {
                errorType = 3;
            } else if (e instanceof IllegalEmptyCheckException) {//非空
                errorType = 4;
            } else if (e instanceof IllegalValueCheckException) {//值域超出
                errorType = 5;
            } else if (e instanceof IllegalTypeCheckException) {//类型
                errorType = 6;
            } else if (e instanceof IllegalFormatCheckException) {//格式
                errorType = 7;
            } else if (e instanceof AnalyzerException) {
                errorType = 21;
            }
            if (esSimplePackage != null) {
                if (StringUtils.isNotBlank(e.getMessage())) {
                    statusReportService.reportStatus(esSimplePackage.get_id(), AnalyzeStatus.Failed, errorType, e.getMessage());
                    logger.error(e.getMessage(), e);
                } else {
                    statusReportService.reportStatus(esSimplePackage.get_id(), AnalyzeStatus.Failed, errorType, "Internal server error, please see task log for detail message.");
                    logger.error("Empty exception message, please see the following detail info.", e);
                }
            } else {
                logger.error("Empty pack cause by:" + e.getMessage());
            }
        } finally {
            if (zipPackage != null) {
                zipPackage.houseKeep();
            }
        }
    }

    public ZipPackage analyze (EsSimplePackage esSimplePackage) throws Throwable {
        long starttime = System.currentTimeMillis();
        ZipPackage zipPackage = null;
        try {
            if (esSimplePackage != null) {
                zipPackage = new ZipPackage(esSimplePackage);
                zipPackage.download();
                zipPackage.unZip();
                ProfileType profileType = zipPackage.resolve();
                if (ProfileType.Standard != profileType && !zipPackage.isReUploadFlg()) {
                    throw new ZipException("Ignore non-standard package file or re-upload package file");
                }
                packageQcService.qcHandle(zipPackage);
            }
        } finally {
            if (zipPackage != null) {
                zipPackage.houseKeep();
            }
        }
        long endtime = System.currentTimeMillis();
        System.out.println("耗时：" + (endtime - starttime) + "ms");
        return zipPackage;
    }

    public void esSaveData(String index, String type, String dataList) throws Exception {
        List<Map<String, Object>> list = objectMapper.readValue(dataList, List.class);
        elasticSearchUtil.bulkIndex(index, type, list);
    }
}
