package com.yihu.ehr.profile.core.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.core.StdDataSet;
import com.yihu.ehr.profile.core.lightweight.LightWeightDataSet;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 简易数据集解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Component
public class DataSetResolver {

    /**
     * 结构化档案包数据集处理
     * @param jsonNode
     * @param isOrigin
     * @return
     */
    public StdDataSet parseStructuredJsonDataSet(JsonNode jsonNode, boolean isOrigin) {
        StdDataSet dataSet = new StdDataSet();

        try {
            assert jsonNode != null;

            String version = jsonNode.get("inner_version").asText();
            String dataSetCode = jsonNode.get("code").asText();
            String eventNo = jsonNode.get("event_no").asText();
            String patientId = jsonNode.get("patient_id").asText();
            String orgCode = jsonNode.get("org_code").asText();
            String eventDate = jsonNode.path("event_time").asText();        // 旧数据集结构可能不存在这个属性

            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(version);
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(orgCode);

            JsonNode jsonRecords = jsonNode.get("data");
            for (int i = 0; i < jsonRecords.size(); ++i) {
                Map<String, String> record = new HashMap<>();

                JsonNode jsonRecord = jsonRecords.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = jsonRecord.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaData = item.getKey();
                    if (metaData.equals("PATIENT_ID") || metaData.equals("EVENT_NO")) continue;

                    String[] qualifiedMetaData = translateMetaData(
                            version, dataSetCode, metaData,
                            item.getValue().asText().equals("null") ? "" : item.getValue().asText(),
                            isOrigin);

                    if(qualifiedMetaData != null){
                        record.put(qualifiedMetaData[0], qualifiedMetaData[1]);
                        if (qualifiedMetaData.length > 2)
                            record.put(qualifiedMetaData[2], qualifiedMetaData[3]);
                    }
                }

                dataSet.addRecord(Integer.toString(i), record);
            }
        } catch (NullPointerException ex) {
            throw new RuntimeException("Null pointer occurs while generate data set");
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
        }

        return dataSet;
    }


    /**
     * 轻量级档案包数据集处理
     * @param structuredProfile
     * @param jsonNode
     * @throws JsonProcessingException
     * @throws ParseException
     */
    public void  parseLightJsonDataSet(StructuredProfile structuredProfile, JsonNode jsonNode) throws JsonProcessingException, ParseException {

        LightWeightDataSet lightWeightDataSet = new LightWeightDataSet();
        String version = jsonNode.get("inner_version").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String patientId = jsonNode.get("patient_id").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String eventDate = jsonNode.get("event_time").asText();        // 旧数据集结构可能不存在这个属性
        String sumary = jsonNode.get("sumary").toString();

        structuredProfile.setPatientId(patientId);
        structuredProfile.setEventNo(eventNo);
        structuredProfile.setOrgCode(orgCode);
        structuredProfile.setCdaVersion(version);
        structuredProfile.setSummary(sumary);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        structuredProfile.setEventDate(format.parse(eventDate));

        JsonNode dataSets = jsonNode.get("dataset");
        Iterator<Map.Entry<String, JsonNode>> iterator = dataSets.fields();
        while (iterator.hasNext()) {

            Map.Entry<String, JsonNode> map = iterator.next();
            String code = map.getKey();
            String path = map.getValue().asText();
            lightWeightDataSet.setCode(code);
            lightWeightDataSet.setUrl(path);
            structuredProfile.addLightWeightDataSet(code, lightWeightDataSet);
        }
    }

    /**
     * 翻译数据元。
     *
     * @param innerVersion
     * @param dataSetCode
     * @param isOrigin
     * @param metaData
     * @param actualData
     * @return
     */
    protected String[] translateMetaData(String innerVersion,
                                         String dataSetCode,
                                         String metaData,
                                         String actualData,
                                         boolean isOrigin) {
        return new String[]{metaData, actualData};
    }
}
