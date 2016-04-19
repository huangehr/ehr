package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.extractor.EventExtractor;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.commons.DataSetTableOption;
import com.yihu.ehr.profile.core.structured.StructuredDataSet;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
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
public class StructuredPackageResolver {
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
    private final static String IndexFolder = "index";
    private final static String DocumentFolder = "document";
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
    public StructuredProfile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }
        StructuredProfile structuredProfile = new StructuredProfile();          //结构化档案

        File[] files = root.listFiles();

        for(File file:files){
            String folderName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1);
            structuredProfile = structuredDataSetParse(structuredProfile, file.listFiles(),folderName);
        }

        makeEventSummary(structuredProfile);

        houseKeep(zipFile, root);

        return structuredProfile;
    }



    /**
     * 结构化档案包解析JSON文件中的数据。
     * @param structuredProfile
     * @param files
     * @throws IOException
     */
    public StructuredProfile structuredDataSetParse(StructuredProfile structuredProfile, File[] files, String folderName) throws ParseException, IOException {
        for (File file : files) {
            String lastName = folderName.substring(folderName.lastIndexOf("\\")+1);
            StructuredDataSet dataSet = generateDataSet(file, lastName.equals(OriFolder) ? true :false);

            // 原始数据存储在表"数据集代码_ORIGIN"
            String dataSetTable = lastName.equals(OriFolder) ? DataSetTableOption.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            structuredProfile.addDataSet(dataSetTable, dataSet);
            structuredProfile.setPatientId(dataSet.getPatientId());
            structuredProfile.setEventNo(dataSet.getEventNo());
            structuredProfile.setOrgCode(dataSet.getOrgCode());
            structuredProfile.setCdaVersion(dataSet.getCdaVersion());

            dataSet.setCode(dataSetTable);

            // Extract key data from data set if exists
            if (!lastName.equals(OriFolder)) {
                if (structuredProfile.getCardId().length() == 0) {
                    Object object = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    if (null != object) {
                        Properties properties = (Properties) object;
                        structuredProfile.setCardId(properties.getProperty("CardNo"));
                    }
                }

                if (StringUtils.isEmpty(structuredProfile.getDemographicId())) {
                    structuredProfile.setDemographicId((String) extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.DemographicInfo));
                }

                if (structuredProfile.getEventDate() == null) {
                    structuredProfile.setEventDate((Date) extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventDate));
                }
            }
            structuredProfile.addDataSet(dataSet.getCode(), dataSet);
        }
        return structuredProfile;

    }



    /**
     * 生产数据集
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    public StructuredDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        StructuredDataSet dataSet = dataSetResolverWithTranslator.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }


    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param structuredProfile
     */
    public void makeEventSummary(StructuredProfile structuredProfile) {
        EventExtractor eventExtractor = context.getBean(EventExtractor.class);

        for (String dataSetTable : structuredProfile.getDataSetTables()) {
            if (StringUtils.isEmpty(structuredProfile.getSummary()) && eventExtractor.getDataSets().containsKey(dataSetTable)) {
                structuredProfile.setSummary(eventExtractor.getDataSets().get(dataSetTable));
            }

            int rowIndex = 0;
            StructuredDataSet dataSet = structuredProfile.getDataSet(dataSetTable);
            String[] rowKeys = new String[dataSet.getRecordKeys().size()];
            dataSet.getRecordKeys().toArray(rowKeys);
            for (String rowKey : rowKeys) {
                dataSet.updateRecordKey(rowKey, structuredProfile.getId() + "$" + rowIndex++);
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
