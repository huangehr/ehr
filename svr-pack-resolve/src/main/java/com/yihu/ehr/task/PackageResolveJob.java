package com.yihu.ehr.task;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.persist.ProfileService;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.service.PackageResolver;
import com.yihu.ehr.util.log.LogService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
@Service
public class PackageResolveJob implements InterruptableJob {
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        doResolve("OLDEST");
    }

    public void execute(String packageId){
        doResolve(packageId);
    }

    private void doResolve(String packageId){
        try{
            XPackageMgrClient packageMgrClient = SpringContext.getService(XPackageMgrClient.class);
            PackageResolver resolver = SpringContext.getService(PackageResolver.class);
            ProfileService profileService = SpringContext.getService(ProfileService.class);

            MPackage pack = packageMgrClient.getPackage(packageId);
            if (pack == null) return;

            String zipFile = downloadTo(pack.getRemotePath());

            Profile profile = resolver.doResolve(pack, zipFile);
            profileService.saveProfile(profile);

            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                    "Identity: " + profile.getDemographicId() + ", profile: " + profile.getId());
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
