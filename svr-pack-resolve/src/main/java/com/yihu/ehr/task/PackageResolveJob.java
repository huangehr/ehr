package com.yihu.ehr.task;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.mq.MessageBuffer;
import com.yihu.ehr.profile.memory.intermediate.MemoryProfile;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.service.PackageResolveEngine;
import com.yihu.ehr.util.log.LogService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import java.util.NoSuchElementException;

/**
 * 档案包解析作业。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
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

            LogService.getLogger().info("Package resolve job start: package " + pack.getId());

            PackageResolveEngine resolveEngine = SpringContext.getService(PackageResolveEngine.class);
            XPackageMgrClient packageMgrClient = SpringContext.getService(XPackageMgrClient.class);
            ProfileService profileService = SpringContext.getService(ProfileService.class);

            String zipFile = downloadTo(pack.getRemotePath());

            MemoryProfile profile = resolveEngine.doResolve(pack, zipFile);
            profileService.saveProfile(profile);

            packageMgrClient.reportStatus(pack.getId(),
                    ArchiveStatus.Finished,
                    String.format("Rowkey: %s, identity: %s", profile.getId(), profile.getDemographicId()));

            LogService.getLogger().info("Package resolve job done: package " + pack.getId());

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
