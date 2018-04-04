package com.yihu.ehr.resolve.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.resolve.feign.PackageMgrClient;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.resource.stage1.PackageResolveService;
import com.yihu.ehr.resolve.service.resource.stage2.ArchivingService;
import com.yihu.ehr.resolve.service.resource.stage2.PackMillService;
import com.yihu.ehr.resolve.service.resource.stage2.ResourceService;
import com.yihu.ehr.resolve.util.PackResolveLogger;
import com.yihu.ehr.util.datetime.DateUtil;
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
public class PackageResourceJob implements InterruptableJob {

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir") + java.io.File.separator;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) {
        //弃用消息接口
        //MessageBuffer messageBuffer = SpringContext.getService(MessageBuffer.class);
        //MPackage pack =  messageBuffer.getMessage();
        //pack = packageMgrClient.getResolvePackage();

        PackageMgrClient packageMgrClient = SpringContext.getService(PackageMgrClient.class);
        //该对象要采用名称的方式获取，否则：expected single matching bean but found 3: redisTemplate,sessionRedisTemplate,stringRedisTemplate
        RedisTemplate<String, Serializable> redisTemplate = SpringContext.getService("redisTemplate");
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        Serializable serializable = redisTemplate.opsForList().rightPop(RedisCollection.PackageList);
        MPackage pack = null;
        try {
            if(serializable != null) {
                String packStr = serializable.toString();
                pack = objectMapper.readValue(packStr, MPackage.class);
            }
            if (pack != null) {
                packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Acquired, "正在入库中");
                PackResolveLogger.info("开始入库:" + pack.getId() + ", Timestamp:" + new Date());
                doResolve(pack, packageMgrClient);
            }
        } catch (Exception e) {
            if (pack != null) {
                try {
                    if (StringUtils.isBlank(e.getMessage())) {
                        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Failed, "Internal server error, please see task log for detail message.");
                        PackResolveLogger.error("Internal server error, please see task log for detail message.", e);
                    } else {
                        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Failed, e.getMessage());
                        PackResolveLogger.error(e.getMessage());
                    }
                } catch (Exception e1) {
                    PackResolveLogger.error(e1.getMessage());
                }
            }
        }
    }

    private void doResolve(MPackage pack, PackageMgrClient packageMgrClient) throws Exception {
        PackageResolveService resolveEngine = SpringContext.getService(PackageResolveService.class);
        PackMillService packMill = SpringContext.getService(PackMillService.class);
        ArchivingService archivingService = SpringContext.getService(ArchivingService.class);
        ResourceService resourceService = SpringContext.getService(ResourceService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        //long start = System.currentTimeMillis();
        StandardPackage standardPackage = resolveEngine.doResolve(pack, downloadTo(pack.getRemotePath()));
        ResourceBucket resourceBucket = packMill.grindingPackModel(standardPackage);
        archivingService.archiving(resourceBucket, standardPackage);
        resourceService.save(resourceBucket, standardPackage);
        //回填入库状态
        Map<String,String> map = new HashMap();
        map.put("profileId", standardPackage.getId());
        map.put("demographicId", standardPackage.getDemographicId());
        map.put("eventType", standardPackage.getEventType() == null? "":String.valueOf(standardPackage.getEventType().getType()));
        map.put("eventNo", standardPackage.getEventNo());
        map.put("eventDate", DateUtil.toStringLong(standardPackage.getEventDate()));
        map.put("patientId", standardPackage.getPatientId());
        map.put("reUploadFlg", String.valueOf(standardPackage.isReUploadFlg()));
        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished, objectMapper.writeValueAsString(map));
    }

    private String downloadTo(String filePath) throws Exception {
        FastDFSUtil fastDFSUtil = SpringContext.getService(FastDFSUtil.class);
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }

}
