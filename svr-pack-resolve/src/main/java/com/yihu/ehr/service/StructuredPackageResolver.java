package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.core.commons.DataSetTableOption;
import com.yihu.ehr.profile.core.structured.StructuredDataSet;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
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

    @Autowired
    private FastDFSUtil fastDFSUtil;

    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");
    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String IndexFolder = "index";
    private final static String DocumentFolder = "document";
    private final static String JsonExt = ".json";


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
            file.delete();
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

}
