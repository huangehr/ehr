package com.yihu.ehr.task;

import com.codahale.metrics.MetricRegistry;
import com.yihu.ehr.config.MetricNames;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.queue.MessageBuffer;
import com.yihu.ehr.service.resource.stage1.PackageResolveEngine;
import com.yihu.ehr.service.resource.stage1.StdPackModel;
import com.yihu.ehr.service.resource.stage2.PackMill;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.resource.stage2.ResourceService;
import com.yihu.ehr.service.util.LegacyPackageException;
import com.yihu.ehr.util.log.LogService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;

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
        PackMill packMill = SpringContext.getService(PackMill.class);
        ResourceService resourceService = SpringContext.getService(ResourceService.class);

        try {
            if (pack == null) return;

            long start = System.currentTimeMillis();

            StdPackModel stdPackModel = resolveEngine.doResolve(pack, downloadTo(pack.getRemotePath()));
            ResourceBucket resourceBucket = packMill.grindingPackModel(stdPackModel);
            resourceService.save(resourceBucket);

            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                    String.format("Profile: %s, identity: %s", resourceBucket.getId(), resourceBucket.getDemographicId()));

            getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);
        } catch(LegacyPackageException e){
            packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.LegacyIgnored, e.getMessage());
        } catch (Throwable throwable) {
            LogService.getLogger().error("Package resolve job error: package " + throwable.getMessage());
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
