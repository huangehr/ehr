package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.util.ProfileGenerator;
import com.yihu.ehr.profile.core.StdDataSet;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

/**
 * 标准档案包解析器.
 *
 * @author Sand
 * @created 2015.09.09 15:04
 */
@Component
public class StdPackageResolver extends PackageResolver {
    @Override
    public void resolve(StdProfile profile, File root) throws IOException, ParseException {
        File standardFolder = new File(root.getAbsolutePath() + File.separator + ProfileGenerator.StandardFolder);
        parseFiles(profile, standardFolder.listFiles(), false);

        File originFolder = new File(root.getAbsolutePath() + File.separator + ProfileGenerator.OriginFolder);
        parseFiles(profile, originFolder.listFiles(), true);
    }

    /**
     * 结构化档案包解析JSON文件中的数据。
     */
    private void parseFiles(StdProfile profile, File[] files, boolean origin) throws ParseException, IOException {
        for (File file : files) {
            StdDataSet dataSet = generateDataSet(file, origin);

            String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            dataSet.setCode(dataSetCode);

            // Extract key data from data set if exists
            if (!origin) {
                if (StringUtils.isEmpty(profile.getCardId())) {
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

            profile.setPatientId(dataSet.getPatientId());
            profile.setEventNo(dataSet.getEventNo());
            profile.setOrgCode(dataSet.getOrgCode());
            profile.setCdaVersion(dataSet.getCdaVersion());
            profile.setCreateDate(dataSet.getCreateDate());
            profile.insertDataSet(dataSetCode, dataSet);
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
    private StdDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        StdDataSet dataSet = dataSetResolverWithTranslator.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }
}
