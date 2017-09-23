package com.yihu.ehr.service.task;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.config.MetricNames;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.feign.XPatientEndClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.queue.MessageBuffer;
import com.yihu.ehr.service.resource.stage1.PackageResolveEngine;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage2.PackMill;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.resource.stage2.ResourceService;
import com.yihu.ehr.profile.exception.LegacyPackageException;
import com.yihu.ehr.service.resource.stage2.repo.PatientInfoRepository;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.log.LogService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 档案包解析作业。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
public class PackageResourceJob implements InterruptableJob {

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");
    @Autowired
    private PatientInfoRepository patientInfoRepository;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            MessageBuffer messageBuffer = SpringContext.getService(MessageBuffer.class);
            MPackage pack = messageBuffer.getMessage();

            doResolve(pack);
        } catch (NoSuchElementException e) {
            LogService.getLogger().debug("No package to resolve in queue.");
        }
    }

    private void doResolve(MPackage pack) {
        PackageResolveEngine resolveEngine = SpringContext.getService(PackageResolveEngine.class);
        XPackageMgrClient packageMgrClient = SpringContext.getService(XPackageMgrClient.class);
        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Acquired, "正在入库中");
        System.out.println("-----------------正在入库中:" + pack.getId() + "------------------------");
        PackMill packMill = SpringContext.getService(PackMill.class);
        ResourceService resourceService = SpringContext.getService(ResourceService.class);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (pack == null) return;
            long start = System.currentTimeMillis();
            StandardPackage standardPackage = resolveEngine.doResolve(pack, downloadTo(pack.getRemotePath()));
            ResourceBucket resourceBucket = packMill.grindingPackModel(standardPackage);
            resourceService.save(resourceBucket);

            //回填入库状态
            Map<String,String> map = new HashMap();
            map.put("profileId",standardPackage.getId());
            map.put("demographicId",standardPackage.getDemographicId());
            map.put("eventType",String.valueOf(standardPackage.getEventType().getType()));
            map.put("eventNo",standardPackage.getEventNo());
            map.put("eventDate", DateUtil.toStringLong(standardPackage.getEventDate()));
            map.put("patientId",standardPackage.getPatientId());

            //获取注册信息
            String idCardNo = resourceBucket.getDemographicId() == null ? "":resourceBucket.getDemographicId().toString();
            if(!idCardNo.equals("")) {
                boolean isRegistered = patientInfoRepository.isRegistered(idCardNo);
                if (!isRegistered) {
                    MDemographicInfo demoInfo = new MDemographicInfo();
                    demoInfo.setIdCardNo(resourceBucket.getDemographicId() == null ? "" : resourceBucket.getDemographicId().toString());
                    demoInfo.setName(resourceBucket.getPatientName() == null ? "" : resourceBucket.getPatientName().toString());
                    demoInfo.setBirthday(resourceBucket.getMasterRecord().getResourceValue("EHR_000320") == null ? null : DateTimeUtil.simpleDateParse(resourceBucket.getMasterRecord().getResourceValue("EHR_000320")));
                    demoInfo.setNativePlace(resourceBucket.getMasterRecord().getResourceValue("EHR_000015") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000015").toString());
                    demoInfo.setGender(resourceBucket.getMasterRecord().getResourceValue("EHR_000019") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000019").toString());
                    demoInfo.setMartialStatus(resourceBucket.getMasterRecord().getResourceValue("EHR_000014") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000014").toString());
                    demoInfo.setNation(resourceBucket.getMasterRecord().getResourceValue("EHR_000016") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000016").toString());
                    demoInfo.setTelephoneNo(resourceBucket.getMasterRecord().getResourceValue("EHR_000003") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString());
                    demoInfo.setEmail(resourceBucket.getMasterRecord().getResourceValue("EHR_000008") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000008").toString());
                    //注册
                    boolean isSucceed = patientInfoRepository.save(demoInfo);
                    if (!isSucceed) {
                        LogService.getLogger().info("idCardNo:" + idCardNo + " registration failed !");
                    }
                }
            }else {
                LogService.getLogger().info("idCardNo is empty !");
            }
            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,objectMapper.writeValueAsString(map));
            getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);
        }
        /*catch (LegacyPackageException e) {
            LogService.getLogger().error("Package resolve job error: package " + e.getMessage());          //未能入库的档案
            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.LegacyIgnored, e.getMessage());
        }*/
        catch (Throwable throwable) {
            LogService.getLogger().error("Package resolve job error: package " + throwable.getMessage());
            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Failed, throwable.getMessage());
        }
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
