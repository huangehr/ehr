package com.yihu.ehr.task;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.persist.ProfileService;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.service.PackageResolver;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 11:30
 */
@Component
public class PackageResolveTask {
    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    XPackageMgrClient packageMgrClient;

    @Autowired
    PackageResolver resolver;

    @Autowired
    ProfileService profileService;

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    //@Scheduled(cron = "0/2 * * * * ?")
    public void execute(){
        String packageId = "";
        try{
            MPackage pack = packageMgrClient.getPackage("OLDEST");
            if (pack == null) return;

            String zipFile = downloadTo(pack.getRemotePath());

            Profile profile = resolver.doResolve(pack, zipFile);
            profileService.saveProfile(profile);

            packageMgrClient.reportStatus(packageId, ArchiveStatus.Finished,
                    "Identity: " + profile.getDemographicId() + ", profile: " + profile.getId());
        } catch (Exception e) {
            packageMgrClient.reportStatus(packageId, ArchiveStatus.Failed, e.getMessage());

            LogService.getLogger().error(e.getMessage());
        }
    }

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }
}
