package com.yihu.ehr.resolve.job;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.resolve.config.MetricNames;
import com.yihu.ehr.resolve.feign.PackageMgrClient;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.queue.MessageBuffer;
import com.yihu.ehr.resolve.service.resource.stage1.PackageResolveService;
import com.yihu.ehr.resolve.service.resource.stage2.PackMillService;
import com.yihu.ehr.resolve.service.resource.stage2.PatientRegisterService;
import com.yihu.ehr.resolve.service.resource.stage2.ResourceService;
import com.yihu.ehr.resolve.dao.*;
import com.yihu.ehr.resolve.util.PackResolveLogger;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 档案包解析作业。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
@Component
public class PackageResourceJob implements InterruptableJob {

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) {
        PackageMgrClient packageMgrClient = SpringContext.getService(PackageMgrClient.class);
        MessageBuffer messageBuffer = SpringContext.getService(MessageBuffer.class);
        MPackage pack =  messageBuffer.getMessage();
        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        Scheduler scheduler = context.getScheduler();
        try {
            if (null != pack) {
                PackResolveLogger.info("开始入库:" + pack.getId() + ", Timestamp:" + new Date());
                //LogService.getLogger().info("开始入库:" + pack.getId() + ", Timestamp:" + new Date());
                doResolve(pack, packageMgrClient);
            }
        }catch (Exception e) {
            e.printStackTrace();
            if (StringUtils.isBlank(e.getMessage())) {
                packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Failed, "Internal Server Error");
                PackResolveLogger.error("Internal Server Error, Please See Tomcat Log!");
            }else {
                packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Failed, e.getMessage());
                PackResolveLogger.error(e.getMessage());
            }
            try {
                scheduler.deleteJob(jobKey);
            }catch (SchedulerException se) {
                se.printStackTrace();
                PackResolveLogger.error(se.getMessage());
            }
        }
    }

    private void doResolve(MPackage pack, PackageMgrClient packageMgrClient) throws Exception {
        PackageResolveService resolveEngine = SpringContext.getService(PackageResolveService.class);
        PatientRegisterService patientRegisterService = SpringContext.getService(PatientRegisterService.class);
        PackMillService packMill = SpringContext.getService(PackMillService.class);
        ResourceService resourceService = SpringContext.getService(ResourceService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        long start = System.currentTimeMillis();
        StandardPackage standardPackage = resolveEngine.doResolve(pack, downloadTo(pack.getRemotePath()));
        ResourceBucket resourceBucket = packMill.grindingPackModel(standardPackage);
        //resourceService.save(resourceBucket);
        //居民信息注册
        patientRegisterService.checkPatient(resourceBucket, pack.getId());
        //回填入库状态
        Map<String,String> map = new HashMap();
        map.put("profileId",standardPackage.getId());
        map.put("demographicId",standardPackage.getDemographicId());
        map.put("eventType",String.valueOf(standardPackage.getEventType().getType()));
        map.put("eventNo",standardPackage.getEventNo());
        map.put("eventDate", DateUtil.toStringLong(standardPackage.getEventDate()));
        map.put("patientId",standardPackage.getPatientId());
        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished, objectMapper.writeValueAsString(map));
        getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);
    }

    private String downloadTo(String filePath) throws Exception {
        FastDFSUtil fastDFSUtil = SpringContext.getService(FastDFSUtil.class);
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }

    private MetricRegistry getMetricRegistry(){
        return SpringContext.getService(MetricRegistry.class);
    }
}
