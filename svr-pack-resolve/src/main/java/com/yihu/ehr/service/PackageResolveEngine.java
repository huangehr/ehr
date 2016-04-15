package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.extractor.EventExtractor;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    ObjectMapper objectMapper;

    Map<ProfileType, ProfileResolver> profileResolvers;

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
    public Profile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), TempPath + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }

        Profile profile = ProfileGenerator.generate(root);
        switch (profile.getType()) {
            case Structed:
                profileResolvers.get(ProfileType.Structed).resolve(profile, root);
                break;

            case LightWeight:
                profileResolvers.get(ProfileType.LightWeight).resolve(profile, root);
                break;

            case Nonstructed:
                profileResolvers.get(ProfileType.Nonstructed).resolve(profile, root);
                break;

            default:
                break;
        }

        makeEventSummary(profile);
        houseKeep(zipFile, root);

        return profile;
    }

    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param profile
     */
    public void makeEventSummary(Profile profile) {
        if (profile instanceof StructedProfile){
            StructedProfile structedProfile = (StructedProfile)profile;
            EventExtractor eventExtractor = context.getBean(EventExtractor.class);

            for (String dataSetTable : structedProfile.getDataSetTables()) {
                if (StringUtils.isEmpty(profile.getSummary()) && eventExtractor.getDataSets().containsKey(dataSetTable)) {
                    profile.setSummary(eventExtractor.getDataSets().get(dataSetTable));
                }

                int rowIndex = 0;
                ProfileDataSet dataSet = structedProfile.getDataSet(dataSetTable);
                String[] rowKeys = new String[dataSet.getRecordKeys().size()];
                dataSet.getRecordKeys().toArray(rowKeys);
                for (String rowKey : rowKeys) {
                    dataSet.updateRecordKey(rowKey, profile.getId() + "$" + rowIndex++);
                }
            }
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
    private void init(){
        profileResolvers = new HashMap<>();
        profileResolvers.put(ProfileType.Structed, new StructedProfileResolver());
        profileResolvers.put(ProfileType.LightWeight, new LightWeightProfileResolver());
        profileResolvers.put(ProfileType.Nonstructed, new NonstructedProfileResolver());
    }
}
