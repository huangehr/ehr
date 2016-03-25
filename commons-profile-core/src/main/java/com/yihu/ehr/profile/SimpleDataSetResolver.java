package com.yihu.ehr.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.ObjectId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 简易数据集解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Component
public class SimpleDataSetResolver {
    protected final static Pattern NumberPattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");

    public ProfileDataSet parseJsonDataSet(JsonNode jsonNode, boolean isOrigin) {
        ProfileDataSet dataSet = new ProfileDataSet();

        try {
            assert jsonNode != null;

            String cdaVersion = jsonNode.get("inner_version").asText();
            String code = jsonNode.get("code").asText();
            String eventNo = jsonNode.get("event_no").asText();
            String patientId = jsonNode.get("patient_id").asText();
            String orgCode = jsonNode.get("org_code").asText();
            String eventDate = jsonNode.path("event_time").asText();        // 旧数据集结构可能不存在这个属性

            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(cdaVersion);
            dataSet.setCode(code);
            dataSet.setOrgCode(orgCode);

            JsonNode jsonRecords = jsonNode.get("data");
            for (int i = 0; i < jsonRecords.size(); ++i) {
                Map<String, String> record = new HashMap<>();

                JsonNode jsonRecord = jsonRecords.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = jsonRecord.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key = item.getKey();
                    if (key.equals("PATIENT_ID") || key.equals("EVENT_NO")) continue;

                    if (key.contains("HDSA00_01_017") && !item.getValue().asText().contains("null")) {
                        System.out.println(item.getValue().asText());
                    }

                    String[] standardizedMetaData = standardizeMetaData(
                            cdaVersion,
                            code,
                            key,
                            item.getValue().isNull() ? "" : item.getValue().asText(),
                            isOrigin);
                    if (standardizedMetaData != null) {
                        record.put(standardizedMetaData[0], standardizedMetaData[1]);
                        if (standardizedMetaData.length > 2)
                            record.put(standardizedMetaData[2], standardizedMetaData[3]);
                    }
                }

                dataSet.addRecord(new ObjectId((short) 0, BizObject.StdProfile).toString(), record);
            }
        } catch (NullPointerException ex) {
            throw new RuntimeException("Null pointer occurs in JsonFileParser.generateDataSet");
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
     * @param metaDataInnerCode
     * @param actualData
     * @return
     */
    protected String[] standardizeMetaData(String innerVersion,
                                                String dataSetCode,
                                                String metaDataInnerCode,
                                                String actualData,
                                                boolean isOriginDataSet) {
        return new String[]{metaDataInnerCode, actualData};
    }
}
