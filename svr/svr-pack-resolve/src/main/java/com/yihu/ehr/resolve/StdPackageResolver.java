package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.service.resource.stage1.PackModelFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by progr1mmer on 2018/6/8.
 */
@Component
public class StdPackageResolver extends PackageResolver {

    @Override
    public void resolve(OriginalPackage originalPackage, File root) throws Exception {
        //解析标准数据
        File standardFolder = new File(root.getAbsolutePath() + File.separator + PackModelFactory.StandardFolder);
        parseFiles((StandardPackage) originalPackage, standardFolder.listFiles(), false);
    }

    /**
     * 将标准和原始文件夹中的JSON文件转换为数据集
     * 放入标准档案包中
     * @param standardPackage 标准档案包中
     * @param files 文件夹
     * @param origin 是否为标准文件夹
     * @throws Exception
     */
    private void parseFiles(StandardPackage standardPackage, File[] files, boolean origin) throws Exception {
        List<PackageDataSet> packageDataSetList = new ArrayList<>(files.length);
        //新增补传判断---------------Start---------------
        for (File file : files) {
            PackageDataSet dataSet = generateDataSet(file, origin);
            packageDataSetList.add(dataSet);
            if (dataSet.isReUploadFlg() && !standardPackage.isReUploadFlg()){
                standardPackage.setReUploadFlg(true);
            }
        }
        if (standardPackage.isReUploadFlg()) {
            for (PackageDataSet dataSet : packageDataSetList) {
                String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
                dataSet.setCode(dataSetCode);
                standardPackage.setPatientId(dataSet.getPatientId());
                standardPackage.setEventNo(dataSet.getEventNo());
                standardPackage.setOrgCode(dataSet.getOrgCode());
                standardPackage.setCdaVersion(dataSet.getCdaVersion());
                standardPackage.setCreateDate(dataSet.getCreateTime());
                standardPackage.setEventTime(dataSet.getEventTime());
                standardPackage.insertDataSet(dataSetCode, dataSet);
            }
            return;
        }
        //---------------End---------------
        for (PackageDataSet dataSet : packageDataSetList) {
            //将单个JSON文件转化为单个数据集
            String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            dataSet.setCode(dataSetCode);
            // Extract key data from data set if exists
            if (!origin) {

                //身份信息
                if (StringUtils.isEmpty(standardPackage.getDemographicId()) || StringUtils.isEmpty(standardPackage.getPatientName())) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Identity);
                    String demographicId = (String) properties.get(ResourceCells.DEMOGRAPHIC_ID);
                    String patientName = (String) properties.get(ResourceCells.PATIENT_NAME);
                    String patientAge = (String) properties.get(ResourceCells.PATIENT_AGE);
                    if (!StringUtils.isEmpty(demographicId)) {
                        standardPackage.setDemographicId(demographicId.trim());
                    }
                    if (!StringUtils.isEmpty(patientName)) {
                        standardPackage.setPatientName(patientName);
                    }
                    if (!StringUtils.isEmpty(patientAge)) {
                        standardPackage.setPatientAge(patientAge);
                    }
                }

                //就诊事件信息
                if (standardPackage.getEventType() == null) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                    EventType eventType = (EventType) properties.get(ResourceCells.EVENT_TYPE);
                    if (eventType != null) {
                        standardPackage.setEventType(eventType);
                    }
                }

                //门诊或住院诊断
                if (standardPackage.getDiagnosisCode().size() <= 0 || standardPackage.getDiagnosisName().size() <= 0) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                    Set<String> diagnosisCode = (Set<String>) properties.get(ResourceCells.DIAGNOSIS);
                    Set<String> diagnosisName = (Set<String>) properties.get(ResourceCells.DIAGNOSIS_NAME);
                    if (diagnosisCode.size() > 0) {
                        standardPackage.setDiagnosisCode(diagnosisCode);
                    }
                    if (diagnosisName.size() > 0) {
                        standardPackage.setDiagnosisName(diagnosisName);
                    }
                }

                //就诊卡信息
                if (StringUtils.isEmpty(standardPackage.getCardId()) || StringUtils.isEmpty(standardPackage.getCardType())) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    String cardId = (String) properties.get(ResourceCells.CARD_ID);
                    String cardType = (String) properties.get(ResourceCells.CARD_TYPE);
                    if (!StringUtils.isEmpty(cardId)) {
                        standardPackage.setCardId(cardId);
                    }
                    if (!StringUtils.isEmpty(cardType)) {
                        standardPackage.setCardType(cardType);
                    }
                }

                //科室信息
                if (standardPackage.getDeptCode() == null) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Dept);
                    String deptCode = (String) properties.get(ResourceCells.DEPT_CODE);
                    if (StringUtils.isNotEmpty(deptCode)) {
                        standardPackage.setDeptCode(deptCode);
                    }
                }
            }
            if (null == standardPackage.getPatientId()) {
                standardPackage.setPatientId(dataSet.getPatientId());
            }
            if (null == standardPackage.getEventNo()) {
                standardPackage.setEventNo(dataSet.getEventNo());
            }
            if (null == standardPackage.getOrgCode()) {
                standardPackage.setOrgCode(dataSet.getOrgCode());
            }
            if (null == standardPackage.getCdaVersion()) {
                standardPackage.setCdaVersion(dataSet.getCdaVersion());
            }
            if (null == standardPackage.getCreateDate()) {
                standardPackage.setCreateDate(dataSet.getCreateTime());
            }
            if (null == standardPackage.getEventTime()) {
                standardPackage.setEventTime(dataSet.getEventTime());
            }
            standardPackage.insertDataSet(dataSetCode, dataSet);
        }
    }

    /**
     * 根据JSON文件生产数据集
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    private PackageDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IllegalJsonFileException("Invalid json file when generate data set");
        }
        PackageDataSet dataSet = dataSetParser.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }


}
