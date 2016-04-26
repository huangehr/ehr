package com.yihu.ehr.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.mq.MessageBuffer;
import com.yihu.ehr.profile.core.commons.StructuredProfile;
import com.yihu.ehr.profile.core.nostructured.NonStructedProfile;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import com.yihu.ehr.service.LightWeightPackageResolver;
import com.yihu.ehr.common.PackageUtil;
import com.yihu.ehr.service.StructuredPackageResolver;
import com.yihu.ehr.service.NoStructuredPackageResolver;
import com.yihu.ehr.util.log.LogService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
@Service
public class PackageResolveJob implements InterruptableJob {

    @Autowired
    private StructuredPackageResolver structuredPackageResolver;

    @Autowired
    private NoStructuredPackageResolver noStructuredPackageResolver;

    @Autowired
    private LightWeightPackageResolver lightWeightPackageResolver;

    @Autowired
    ObjectMapper objectMapper;

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try{
            MessageBuffer messageBuffer = SpringContext.getService(MessageBuffer.class);
            MPackage pack = messageBuffer.getMessage();

            doResolve(pack);
        } catch (NoSuchElementException e){
            LogService.getLogger().debug("No package to resolve in queue.");
        }
    }

    private void doResolve(MPackage pack){
        try{
            if (pack == null) return;

            LogService.getLogger().info("Quartz job: resolve package " + pack.getId());

            XPackageMgrClient packageMgrClient = SpringContext.getService(XPackageMgrClient.class);
            PackageUtil packageUtil = SpringContext.getService(PackageUtil.class);
            ProfileRepository profileRepo = SpringContext.getService(ProfileRepository.class);

            String zipFile = downloadTo(pack.getRemotePath());


            StructuredProfile structuredProfile = null;           //结构化档案
            NonStructedProfile noStructuredProfile;                        //非结构化档案

            ProfileType profileType = packageUtil.getProfileType(pack, zipFile);
            if (profileType == ProfileType.Structured) {
                structuredProfile = structuredPackageResolver.doResolve(pack, zipFile);
                profileRepo.saveStructuredProfileModel(structuredProfile);
                packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                        "Identity: " + structuredProfile.getDemographicId() + ", structuredProfile: " + structuredProfile.getId());
            } else if (profileType == ProfileType.NonStructured) {
                noStructuredProfile = noStructuredPackageResolver.doResolve(pack, zipFile);
                profileRepo.saveUnStructuredProfile(noStructuredProfile);
                packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                        "Identity: " + noStructuredProfile.getDemographicId() + ", unStructuredProfile: " + noStructuredProfile.getId());
            } else if (profileType == ProfileType.Lightweight) {
                structuredProfile = lightWeightPackageResolver.doResolve(pack, zipFile);
                profileRepo.saveStructuredProfileModel(structuredProfile);
                packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                        "Identity: " + structuredProfile.getDemographicId() + ", lightWeightProfile: " + structuredProfile.getId());
            }

        } catch (Exception e) {
            LogService.getLogger().error(e.getMessage());
        }
    }

    private String downloadTo(String filePath) throws Exception {
        FastDFSUtil fastDFSUtil = SpringContext.getService(FastDFSUtil.class);

        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }
}
