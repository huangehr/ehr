package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.PackageMgrClient;
import com.yihu.ehr.analyze.service.qc.PackageQcService;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.AnalyzeStatus;
import com.yihu.ehr.profile.exception.IllegalEmptyCheckException;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.exception.IllegalValueCheckException;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private static final String INDEX = "json_archives";
    private static final String QC_DATASET_INFO = "qc_dataset_info";
    private static final String QC_METADATA_INFO = "qc_metadata_info";

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private PackQueueService packQueueService;
    @Autowired
    private PackageMgrClient packageMgrClient;
    @Autowired
    private PackageQcService packageQcService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

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
        EsSimplePackage esSimplePackage = null;
        ZipPackage zipPackage = null;
        try {
            esSimplePackage = packQueueService.pop();
            if (esSimplePackage != null) {
                packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Acquired, 0, "正在质控中");
                zipPackage = new ZipPackage(esSimplePackage);
                zipPackage.download();
                zipPackage.unZip();
                zipPackage.resolve();
                packageQcService.qcHandle(zipPackage);
                //保存数据集质控数据
                //elasticSearchUtil.bulkIndex(INDEX, QC_DATASET_INFO, zipPackage.getQcMetadataRecords());
                //保存数据元质控数据
                //elasticSearchUtil.bulkIndex(INDEX, QC_METADATA_INFO, zipPackage.getQcMetadataRecords());
                //报告质控状态
                //packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Finished, 0, "qc success");
                //发送解析消息
                //packQueueService.push(esSimplePackage);
            }
        } catch (Exception e) {
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
            }
            if (esSimplePackage != null) {
                try {
                    if (StringUtils.isNotBlank(e.getMessage())) {
                        packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Failed, errorType, e.getMessage());
                        logger.error(e.getMessage(), e);
                    } else {
                        packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Failed, errorType, "Internal server error, please see task log for detail message.");
                        logger.error("Internal server error, please see task log for detail message.", e);
                    }
                } catch (Exception e1) {
                    logger.error("Execute feign fail cause by:" + e1.getMessage());
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

    public Map<String, Object> analyze (EsSimplePackage esSimplePackage) {
        ZipPackage zipPackage = null;
        Map<String, Object> result = new HashMap<>();
        try {
            if (esSimplePackage != null) {
                //packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Acquired, 0, "正在质控中");
                zipPackage = new ZipPackage(esSimplePackage);
                zipPackage.download();
                zipPackage.unZip();
                zipPackage.resolve();
                packageQcService.qcHandle(zipPackage);
                //数据集质控数据
                result.put("qc_dataset_info", zipPackage.getQcDataSetRecord());
                //数据元质控数据
                result.put("qc_metadata_info", zipPackage.getQcMetadataRecords());
                //报告质控状态
                //packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Finished, 0, "qc success");
                //发送解析消息
                //packQueueService.push(esSimplePackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            /*int errorType = -1;
            if (e instanceof ZipException) {
                errorType = 1;
            } else if (e instanceof IllegalJsonFileException) {
                errorType = 2;
            } else if (e instanceof IllegalJsonDataException) {
                errorType = 3;
            }
            if (esSimplePackage != null) {
                try {
                    if (StringUtils.isNotBlank(e.getMessage())) {
                        packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Failed, errorType, e.getMessage());
                        logger.error(e.getMessage(), e);
                    } else {
                        packageMgrClient.analyzeStatus(esSimplePackage.get_id(), AnalyzeStatus.Failed, errorType, "Internal server error, please see task log for detail message.");
                        logger.error("Internal server error, please see task log for detail message.", e);
                    }
                } catch (Exception e1) {
                    logger.error("Execute feign fail cause by:" + e1.getMessage());
                }
            } else {
                logger.error("Empty pack cause by:" + e.getMessage());
            }*/
        } finally {
            if (zipPackage != null) {
                zipPackage.houseKeep();
            }
        }
        return result;
    }

    public void esSaveData(String index, String type, String dataList) throws Exception {
        List<Map<String, Object>> list = objectMapper.readValue(dataList, List.class);
        elasticSearchUtil.bulkIndex(index, type, list);
    }
}
