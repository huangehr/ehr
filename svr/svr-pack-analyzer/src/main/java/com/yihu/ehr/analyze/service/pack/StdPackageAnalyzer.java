package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.analyze.service.RedisService;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 标准档案分析
 *
 * @author Airhead
 * @created 2018.01.16
 */
@Component
public class StdPackageAnalyzer extends PackageAnalyzer {

    public final static String StandardFolder = "standard";
    public final static String OriginFolder = "origin";

    @Autowired
    private RedisService redisService;

    @Override
    public void analyze(ZipPackage zipPackage) throws Exception {
        File root = zipPackage.getPackFile();
        //解析标准数据
        String standardPath = root.getAbsolutePath() + File.separator + StandardFolder;
        File standardFolder = new File(standardPath);
        parseFiles(zipPackage, standardFolder.listFiles(), false);
    }

    /**
     * 将标准和原始文件夹中的JSON文件转换为数据集，
     * 放入标准档案包中
     *
     * @param zipPackage 标准档案包中
     * @param files      文件夹
     * @param origin     是否为标准文件夹
     * @throws Exception
     * @throws IOException
     */
    private void parseFiles(ZipPackage zipPackage, File[] files, boolean origin) throws Exception {
        for (File file : files) {
            PackageDataSet dataSet = analyzeDataSet(zipPackage, file, origin);
            //就诊事件信息
            if (zipPackage.getEventType() == null) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                EventType eventType = (EventType) properties.get(MasterResourceFamily.BasicColumns.EventType);
                if (eventType != null) {
                    zipPackage.setEventType(eventType);
                }
            }
            //门诊或住院诊断
           /* if (standardPackage.getDiagnosisList() == null
                    || standardPackage.getDiagnosisList().size() <= 0
                    || standardPackage.getDiagnosisNameList() == null
                    || standardPackage.getDiagnosisNameList().size() <= 0) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                Set<String> diagnosisList = (Set<String>) properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                Set<String> diagnosisNameList = (Set<String>) properties.get(MasterResourceFamily.BasicColumns.DiagnosisName);
                if (diagnosisList.size() > 0) {
                    standardPackage.setDiagnosisList(diagnosisList);
                }
                if (diagnosisNameList.size() > 0) {
                    standardPackage.setDiagnosisNameList(diagnosisNameList);
                }
            }*/
            zipPackage.insertDataSet(dataSet.getCode(), dataSet);
        }
    }

    /**
     * 根据JSON文件生产数据集
     *
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */

    public PackageDataSet analyzeDataSet(ZipPackage zipPackage, File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IllegalJsonFileException("Invalid json file when generate data set");
        }
        PackageDataSet dataSet = new PackageDataSet();

        String code = jsonNode.get("code") == null? "" : jsonNode.get("code").asText();
        String patientId = jsonNode.get("patient_id") == null ? "" : jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no") == null ? "" : jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code") == null ? "" : jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version") == null ? "" : jsonNode.get("inner_version").asText();
        String createTime = jsonNode.get("create_date") == null ? "" : jsonNode.get("create_date").asText();
        String eventTime = jsonNode.path("event_time") == null ? "" : jsonNode.path("event_time").asText();
        boolean reUploadFlg = jsonNode.path("reUploadFlg") == null ? false : jsonNode.path("reUploadFlg").asBoolean();

        //验证档案基础数据的完整性，当其中某字段为空的情况下直接提示档案包信息缺失。
        StringBuilder errorMsg = new StringBuilder();
        if (StringUtils.isEmpty(code)){
            errorMsg.append("dataSetCode is null;");
        }
        if (StringUtils.isEmpty(patientId)){
            errorMsg.append("patientId is null;");
        }
        if (StringUtils.isEmpty(eventNo) && !"HDSA00_01".equals(code)){
            errorMsg.append("eventNo is null;");
        }
        if (StringUtils.isEmpty(orgCode)){
            errorMsg.append("orgCode is null;");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())){
            throw new IllegalJsonDataException(errorMsg.toString());
        }
        dataSet.setPatientId(patientId);
        dataSet.setEventNo(eventNo);
        dataSet.setCdaVersion(version);
        dataSet.setCode(code);
        dataSet.setOrgCode(orgCode);
        dataSet.setReUploadFlg(reUploadFlg);

        if (StringUtils.isEmpty(zipPackage.getOrgCode())) {
            zipPackage.setOrgCode(orgCode);
            zipPackage.setOrgArea(redisService.getOrgArea(orgCode));
        }
        if (StringUtils.isEmpty(zipPackage.getPatientId())) {
            zipPackage.setPatientId(patientId);
        }
        if (StringUtils.isEmpty(zipPackage.getEventNo())) {
            zipPackage.setEventNo(eventNo);
        }
        if (StringUtils.isEmpty(zipPackage.getEventTime())) {
            zipPackage.setEventTime(eventTime);
        }

        try {
            dataSet.setEventTime(DateUtil.strToDate(eventTime));
            dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));
        } catch (Exception e) {
            throw new IllegalJsonDataException("Invalid date time format in dataset " + code);
        }
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode.size() > 1) {
            dataSet.setMultiRecord(true);
        }
        for (int i = 0; i < dataNode.size(); ++i) {
            MetaDataRecord record = new MetaDataRecord();
            JsonNode recordNode = dataNode.get(i);
            Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> item = iterator.next();
                String metaDataKey = item.getKey();
                String metaDataValue = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                record.putMetaData(metaDataKey, metaDataValue);
            }
            dataSet.addRecord(Integer.toString(i), record);
        }
        return dataSet;
    }
}
