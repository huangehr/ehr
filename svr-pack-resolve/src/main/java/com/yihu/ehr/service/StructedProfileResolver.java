package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

/**
 * 结构化档案解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:32
 */
public class StructedProfileResolver implements ProfileResolver {
    private final static char PathSep = File.separatorChar;

    @Override
    public void resolve(Profile profile, File root) throws IOException, ParseException {
        String basePackagePath = root.getAbsolutePath() + PathSep;

        parseDataSet(profile, new File(basePackagePath + ProfileGenerator.StructedProfileStdFolder).listFiles(), false);
        parseDataSet(profile, new File(basePackagePath + ProfileGenerator.StructedProfileOriFolder).listFiles(), true);
    }

    /**
     * 解析JSON文件中的数据。
     *
     * @param p
     * @param files
     * @throws IOException
     */
    void parseDataSet(Profile p, File[] files, boolean isOriginDataSet) throws IOException, ParseException {
        StructedProfile profile = (StructedProfile)p;
        ExtractorChain extractorChain = SpringContext.getService(ExtractorChain.class);

        for (File file : files) {
            if (!file.getAbsolutePath().endsWith(ProfileGenerator.JsonExt)) continue;

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

    /**
     * 生产数据集
     *
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    public ProfileDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        DataSetResolverWithTranslator dataSetResolver = SpringContext.getService(DataSetResolverWithTranslator.class);

        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        ProfileDataSet dataSet = dataSetResolver.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }
}
