package com.yihu.ehr.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 简易数据集解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Component
public class DataSetResolver {


    //轻量级档案 获取数据集列表并保存到健康档案中
    public void  parseJsonDataSetlight(Profile profile,JsonNode jsonNode) throws JsonProcessingException, ParseException {

        ProfileDataSet profileDataSet = new ProfileDataSet();
        String version = jsonNode.get("inner_version").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String patientId = jsonNode.get("patient_id").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String eventDate = jsonNode.path("event_time").asText();        // 旧数据集结构可能不存在这个属性\

//            profileDataSet.setPatientId(patientId);
//            profileDataSet.setEventNo(eventNo);
//            profileDataSet.setCdaVersion(version);
//            profileDataSet.setOrgCode(orgCode);

        JsonNode dataSets = jsonNode.get("dataset");
        Iterator<Map.Entry<String, JsonNode>> iterator = dataSets.fields();
        while (iterator.hasNext()) {
            profile.setPatientId(patientId);
            profile.setEventNo(eventNo);
            profile.setOrgCode(orgCode);
            profile.setCdaVersion(version);

            //"event_time": "2015-05-23 08:01:42.0"

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            profile.setEventDate(format.parse(eventDate));

            Map.Entry<String, JsonNode> map = iterator.next();
            String code = map.getKey();
            String path = map.getValue().asText();
            profileDataSet.setCode(code);
            profileDataSet.setRemotePath(path);
            profile.addDataSet(code,profileDataSet);
        }
    }

    //全量级档案 解析数据集
    public ProfileDataSet parseJsonDataSet(JsonNode jsonNode, boolean isOrigin) {
        ProfileDataSet dataSet = new ProfileDataSet();

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
     * 翻译数据元。
     *
     * @param innerVersion
     * @param dataSetCode
     * @param isOriginDataSet
     * @param metaData
     * @param actualData
     * @return
     */
    protected String[] translateMetaData(String innerVersion,
                                         String dataSetCode,
                                         String metaData,
                                         String actualData,
                                         boolean isOriginDataSet) {
        return new String[]{metaData, actualData};
    }
}
