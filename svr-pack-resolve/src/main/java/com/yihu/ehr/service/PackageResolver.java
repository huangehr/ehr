package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.extractor.EventExtractor;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.DataSetTableOption;
import com.yihu.ehr.profile.core.Profile;
import com.yihu.ehr.profile.core.ProfileDataSet;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

/**
 * 档案归档任务.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class PackageResolver {
    @Autowired
    ApplicationContext context;

    @Autowired
    DataSetResolverWithTranslator dataSetResolverWithTranslator;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExtractorChain extractorChain;

    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");
    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String JsonExt = ".json";

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 InDoubt 状态，即无法决定该JSON档案的去向，需要人为干预。
     * <p>
     * ObjectMapper Stream API使用，参见：http://wiki.fasterxml.com/JacksonStreamingApi
     */
    public Profile doResolve(MPackage pack, String zipFile) throws IOException, ParseException {
        try{
            File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
            if (root == null || !root.isDirectory() || root.list().length == 0) {
                throw new RuntimeException("Invalid package file, package id: " + pack.getId());
            }

            Profile profile = new Profile();

            String basePackagePath = root.getAbsolutePath();
            parseDataSet(profile, new File(basePackagePath + PathSep + StdFolder).listFiles(), false);

            File originFiles = new File(basePackagePath + PathSep + OriFolder);
            if (originFiles.exists()) {
                parseDataSet(profile, originFiles.listFiles(), true);
            }

            makeEventSummary(profile);

            return profile;
        } catch (ZipException e){
            houseKeep(zipFile, null);

            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 解析JSON文件中的数据。
     *
     * @param profile
     * @param files
     * @throws IOException
     */
    void parseDataSet(Profile profile, File[] files, boolean isOriginDataSet) throws ParseException, IOException {
        for (File file : files) {
            if (!file.getAbsolutePath().endsWith(JsonExt)) continue;

            ProfileDataSet dataSet = generateDataSet(file, isOriginDataSet);

            // 原始数据存储在表"数据集代码_ORIGIN"
            String dataSetTable = isOriginDataSet ? DataSetTableOption.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            profile.addDataSet(dataSetTable, dataSet);
            profile.setPatientId(dataSet.getPatientId());
            profile.setEventNo(dataSet.getEventNo());
            profile.setOrgCode(dataSet.getOrgCode());
            profile.setCdaVersion(dataSet.getCdaVersion());

            dataSet.setCode(dataSetTable);

            // Extract key data from data set if exists
            if (!isOriginDataSet) {
                if (profile.getCardId().length() == 0) {
                    Object object = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    if (null != object) {
                        Properties properties = (Properties) object;
                        profile.setCardId(properties.getProperty("CardNo"));
                    }
                }

                if (StringUtils.isEmpty(profile.getDemographicId())) {
                    profile.setDemographicId((String) extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.DemographicInfo));
                }

                if (profile.getEventDate() == null) {
                    profile.setEventDate((Date) extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventDate));
                }
            }

            profile.addDataSet(dataSet.getCode(), dataSet);
        }
    }

    public ProfileDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        ProfileDataSet dataSet = dataSetResolverWithTranslator.parseJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }

    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param profile
     */
    public void makeEventSummary(Profile profile) {
        EventExtractor eventExtractor = context.getBean(EventExtractor.class);

        for (String dataSetTable : profile.getDataSetTables()) {
            if (StringUtils.isEmpty(profile.getSummary()) && eventExtractor.getDataSets().containsKey(dataSetTable)) {
                profile.setSummary(eventExtractor.getDataSets().get(dataSetTable));
            }

            int rowIndex = 0;
            ProfileDataSet dataSet = profile.getDataSet(dataSetTable);
            String[] rowKeys = new String[dataSet.getRecordKeys().size()];
            dataSet.getRecordKeys().toArray(rowKeys);
            for (String rowKey : rowKeys) {
                dataSet.updateRecordKey(rowKey, profile.getId() + "$" + rowIndex++);
            }
        }
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(PackageResolver.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }
}
