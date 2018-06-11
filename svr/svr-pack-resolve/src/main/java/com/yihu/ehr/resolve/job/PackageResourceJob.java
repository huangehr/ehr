package com.yihu.ehr.resolve.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.ArchiveStatus;
import com.yihu.ehr.profile.exception.ResolveException;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.resolve.feign.PackageMgrClient;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.resource.stage1.ResolveService;
import com.yihu.ehr.resolve.service.resource.stage2.IdentifyService;
import com.yihu.ehr.resolve.service.resource.stage2.PackMillService;
import com.yihu.ehr.resolve.service.resource.stage2.ResourceService;
import com.yihu.ehr.resolve.log.PackResolveLogger;
import com.yihu.ehr.resolve.util.LocalTempPathUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 档案包解析作业。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
@Component
@DisallowConcurrentExecution
public class PackageResourceJob implements InterruptableJob {

    private static final long DAY = 1000 * 60 * 60 * 24;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) {
        PackageMgrClient packageMgrClient = SpringContext.getService(PackageMgrClient.class);
        //该对象要采用名称的方式获取，否则：expected single matching bean but found 3: redisTemplate,sessionRedisTemplate,stringRedisTemplate
        RedisTemplate<String, Serializable> redisTemplate = SpringContext.getService("redisTemplate");
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        Serializable serializable = redisTemplate.opsForList().rightPop(RedisCollection.ResolveQueue);
        EsSimplePackage pack = null;
        try {
            if (serializable != null) {
                String packStr = serializable.toString();
                pack = objectMapper.readValue(packStr, EsSimplePackage.class);
            }
            if (pack != null) {
                PackResolveLogger.info("开始入库:" + pack.get_id() + ", Timestamp:" + new Date());
                packageMgrClient.reportStatus(pack.get_id(), ArchiveStatus.Acquired, 0, "正在入库中");
                doResolve(pack, packageMgrClient);
                redisTemplate.opsForList().leftPush(RedisCollection.ProvincialPlatformQueue, serializable.toString());
            }
        } catch (Exception e) {
            int errorType = -2;
            if (e instanceof ZipException) {
                errorType = 1;
            } else if (e instanceof IllegalJsonFileException) {
                errorType = 2;
            } else if (e instanceof IllegalJsonDataException) {
                errorType = 3;
            } else if (e instanceof ResolveException) {
                errorType = 21; //21以下为质控和解析的公共错误
            }
            if (pack != null) {
                try {
                    if (StringUtils.isNotBlank(e.getMessage())) {
                        packageMgrClient.reportStatus(pack.get_id(), ArchiveStatus.Failed, errorType, e.getMessage());
                        PackResolveLogger.error(e.getMessage(), e);
                    } else {
                        packageMgrClient.reportStatus(pack.get_id(), ArchiveStatus.Failed, errorType, "Internal server error, please see task log for detail message.");
                        PackResolveLogger.error("Internal server error, please see task log for detail message.", e);
                    }
                } catch (Exception e1) {
                    PackResolveLogger.error("Execute feign fail cause by:" + e1.getMessage());
                }
            } else {
                PackResolveLogger.error("Empty pack cause by:" + e.getMessage());
            }
        }
    }

    private void doResolve(EsSimplePackage pack, PackageMgrClient packageMgrClient) throws Exception {
        ResolveService resolveEngine = SpringContext.getService(ResolveService.class);
        PackMillService packMill = SpringContext.getService(PackMillService.class);
        IdentifyService identifyService = SpringContext.getService(IdentifyService.class);
        ResourceService resourceService = SpringContext.getService(ResourceService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        OriginalPackage originalPackage = resolveEngine.doResolve(pack, downloadTo(pack.getRemote_path()));
        ResourceBucket resourceBucket = packMill.grindingPackModel(originalPackage);
        identifyService.identify(resourceBucket, originalPackage);
        resourceService.save(resourceBucket, originalPackage);
        //回填入库状态
        Map<String, Object> map = new HashMap();
        map.put("defect", resourceBucket.getQcMetadataRecords().getRecords().isEmpty() ? 0 : 1); //是否解析异常
        map.put("patient_name", resourceBucket.getBasicRecord(ResourceCells.PATIENT_NAME));
        map.put("profile_id", resourceBucket.getId());
        map.put("demographic_id", resourceBucket.getBasicRecord(ResourceCells.DEMOGRAPHIC_ID));
        map.put("event_type", originalPackage.getEventType() == null ? -1 : originalPackage.getEventType().getType());
        map.put("event_no", originalPackage.getEventNo());
        map.put("event_date", DateUtil.toStringLong(originalPackage.getEventTime()));
        map.put("patient_id", originalPackage.getPatientId());
        map.put("dept", resourceBucket.getBasicRecord(ResourceCells.DEPT_CODE));
        long delay = pack.getReceive_date().getTime() - originalPackage.getEventTime().getTime();
        map.put("delay", delay % (1000 * 60 * 60 * 24) > 0 ? delay / (1000 * 60 * 60 * 24) + 1 : delay / (1000 * 60 * 60 * 24));
        map.put("re_upload_flg", String.valueOf(originalPackage.isReUploadFlg()));
        packageMgrClient.reportStatus(pack.get_id(), ArchiveStatus.Finished, 0, objectMapper.writeValueAsString(map));
    }

    private String downloadTo(String filePath) throws Exception {
        FastDFSUtil fastDFSUtil = SpringContext.getService(FastDFSUtil.class);
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPathUtil.getTempPathWithUUIDSuffix());
    }

}
