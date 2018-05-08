package com.yihu.ehr.resolve.service.resource.stage1;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.resolve.*;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.yihu.ehr.constants.ProfileType.*;

/**
 * 档案解析引擎.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Service
public class PackageResolveService {

    private final static String TempPath = System.getProperty("java.io.tmpdir") + java.io.File.separator;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ImmediateDataResolver immediateDataResolver;
    private Map<ProfileType, PackageResolver> packageResolvers;

    @PostConstruct
    private void init() {
        packageResolvers = new HashMap<>();
        packageResolvers.put(Standard, context.getBean(StdPackageResolver.class));
        packageResolvers.put(File, context.getBean(FilePackageResolver.class));
        packageResolvers.put(Link, context.getBean(LinkPackageResolver.class));
        packageResolvers.put(DataSet, context.getBean(DataSetPackageResolver.class));
    }

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 Failed 状态，即无法决定该JSON档案的去向，需要人为干预。
     */
    public StandardPackage doResolve(EsSimplePackage pack, String zipFile) throws Exception {
        File root = null;
        try {
            root = new Zipper().unzipFile(new File(zipFile), TempPath + pack.get_id(), pack.getPwd());
            if (root == null || !root.isDirectory() || root.list().length == 0) {
                throw new ZipException("Invalid package file.");
            }
            //根据压缩包获取标准档案包
            StandardPackage standardPackage = PackModelFactory.createPackModel(root);
            PackageResolver packageResolver;
            switch (standardPackage.getProfileType()) {
                case Standard:
                    packageResolver = packageResolvers.get(ProfileType.Standard);
                    break;
                case File:
                    packageResolver = packageResolvers.get(ProfileType.File);
                    break;
                case Link:
                    packageResolver = packageResolvers.get(ProfileType.Link);
                    break;
                case DataSet:
                    packageResolver = packageResolvers.get(ProfileType.DataSet);
                    break;
                default:
                    packageResolver = null;
                    break;
            }
            packageResolver.resolve(standardPackage, root);
            standardPackage.setClientId(pack.getClient_id());
            standardPackage.regularRowKey();
            //profile.determineEventType();
            return standardPackage;
        } finally {
            houseKeep(zipFile, root);
        }
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(PackageResolveService.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }

    /* -------------------- 以下是 即时交互的档案入库 ------------------------------*/

    /**
     * 即时交互档案数据解析入库流程
     * 1. 解析哥哥数据集数据
     * 2. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 3. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finis
     *
     * @param data     档案数据
     * @param clientId 应用ID
     * @return
     * @throws Exception
     */
    public StandardPackage doResolveImmediateData(String data, String clientId) throws Exception {
        StandardPackage standardPackage = new StandardPackage();
        immediateDataResolver.resolve(standardPackage, data);
        standardPackage.setClientId(clientId);
        standardPackage.regularRowKey();
        return standardPackage;
    }

}
