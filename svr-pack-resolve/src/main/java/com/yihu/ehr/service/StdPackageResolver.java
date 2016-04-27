package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.common.PackageUtil;
import com.yihu.ehr.constants.ProfileConstant;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.core.extractor.EventExtractor;
import com.yihu.ehr.profile.core.extractor.KeyDataExtractor;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.StdDataSet;
import com.yihu.ehr.profile.core.commons.DataSetUtil;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.profile.core.commons.StructuredProfile;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
 * @created 2015.09.09 15:04
 */
@Component
public class StdPackageResolver extends PackageResolver {
    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    @Override
    public void resolve(Profile profile, File root) throws IOException, ParseException {

    }

    public Profile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }
        StructuredProfile structuredProfile = new StructuredProfile();          //结构化档案
        structuredProfile.setProfileType(ProfileType.Structured);

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
            StdDataSet dataSet = generateDataSet(file, lastName.equals(ProfileConstant.OriFolder) ? true :false);

            // 原始数据存储在表"数据集代码_ORIGIN"
            String dataSetTable = lastName.equals(ProfileConstant.OriFolder) ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            structuredProfile.addFullWeightDataSet(dataSetTable, dataSet);
            structuredProfile.setPatientId(dataSet.getPatientId());
            structuredProfile.setEventNo(dataSet.getEventNo());
            structuredProfile.setOrgCode(dataSet.getOrgCode());
            structuredProfile.setCdaVersion(dataSet.getCdaVersion());

            dataSet.setCode(dataSetTable);

            // Extract key data from data set if exists
            if (!lastName.equals(ProfileConstant.OriFolder)) {
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
            structuredProfile.addFullWeightDataSet(dataSet.getCode(), dataSet);
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
    public StdDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        StdDataSet dataSet = dataSetResolverWithTranslator.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }


    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param structuredProfile
     */
    public void makeEventSummary(StructuredProfile structuredProfile) {
        EventExtractor eventExtractor = context.getBean(EventExtractor.class);

        for (String dataSetTable : structuredProfile.getFullWeightDataTables()) {
            if (StringUtils.isEmpty(structuredProfile.getSummary()) && eventExtractor.getDataSets().containsKey(dataSetTable)) {
                structuredProfile.setSummary(eventExtractor.getDataSets().get(dataSetTable));
            }

            int rowIndex = 0;
            StdDataSet dataSet = structuredProfile.getFullWeightData(dataSetTable);
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
            LogService.getLogger(PackageUtil.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }
}
