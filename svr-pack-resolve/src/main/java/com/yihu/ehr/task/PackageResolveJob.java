package com.yihu.ehr.task;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.mq.MessageBuffer;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import com.yihu.ehr.service.PackageResolver;
import com.yihu.ehr.util.log.LogService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
            PackageResolver resolver = SpringContext.getService(PackageResolver.class);
            ProfileRepository profileRepository = SpringContext.getService(ProfileRepository.class);

            String zipFile = downloadTo(pack.getRemotePath());

            Profile structuredProfile = resolver.doResolve(pack, zipFile);
            //// TODO: 2016/4/15  
            //profileRepository.save(structuredProfile);

            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                    "Identity: " + structuredProfile.getDemographicId() + ", profile: " + structuredProfile.getId());
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
