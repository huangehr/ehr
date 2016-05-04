package com.yihu.ehr.service;

import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.util.ProfileFactory;
import com.yihu.ehr.profile.core.ProfileType;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.yihu.ehr.profile.core.ProfileType.*;

/**
 * 档案解析引擎.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class PackageResolveEngine {
    @Autowired
    ApplicationContext context;

    Map<ProfileType, PackageResolver> packageResolvers;

    private final static String TempPath = System.getProperty("java.io.tmpdir") + File.separatorChar;

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 Failed 状态，即无法决定该JSON档案的去向，需要人为干预。
     */
    public StdProfile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = null;
        try {
            root = new Zipper().unzipFile(new File(zipFile), TempPath + pack.getId(), pack.getPwd());
            if (root == null || !root.isDirectory() || root.list().length == 0) {
                throw new RuntimeException("Invalid package file, package id: " + pack.getId());
            }

            StdProfile profile = ProfileFactory.generate(root);
            PackageResolver packageResolver;
            switch (profile.getProfileType()) {
                case Standard:
                    packageResolver = packageResolvers.get(ProfileType.Standard);
                    break;

                case Document:
                    packageResolver = packageResolvers.get(ProfileType.Document);
                    break;

                case Link:
                    packageResolver = packageResolvers.get(ProfileType.Link);
                    break;

                default:
                    packageResolver = null;
                    break;
            }

            packageResolver.resolve(profile, root);

            profile.setClientId(pack.getClientId());
            profile.regularRowKey();
            profile.determineEventType();

            return profile;
        } finally {
            houseKeep(zipFile, root);
        }
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(PackageResolveEngine.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }

    @PostConstruct
    private void init() {
        packageResolvers = new HashMap<>();
        packageResolvers.put(Standard, context.getBean(StdPackageResolver.class));
        packageResolvers.put(Document, context.getBean(DocumentPackageResolver.class));
        packageResolvers.put(Link, context.getBean(LinkPackageResolver.class));
    }
}
