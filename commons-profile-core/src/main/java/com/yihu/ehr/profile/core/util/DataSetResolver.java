package com.yihu.ehr.profile.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.core.profile.DataRecord;
import com.yihu.ehr.profile.core.profile.StdDataSet;
import com.yihu.ehr.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * 简易数据集解析器。仅将JSON结构中的数据集解析成KEY-VALUE模式，不提供字段翻译功能，
 * 例如将类型为S1的数据元添加_S后缀。
 *
 * 此类用于接口接受到使用数据集提供数据的情况。若是档案包中的数据集解析并存入HBase，请使用其派生类。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Component
public class DataSetResolver {

    /**
     * 结构化档案包数据集处理
     *
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
            String createDate = jsonNode.get("create_date").asText();
            String eventDate = jsonNode.path("event_time").asText();    // 旧数据集结构可能不存在这个属性

            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(version);
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(orgCode);
            dataSet.setCreateDate(DateTimeUtils.simpleDateParse(createDate));

            JsonNode jsonRecords = jsonNode.get("data");
            for (int i = 0; i < jsonRecords.size(); ++i) {
                DataRecord record = new DataRecord();

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
                        record.putMetaData(qualifiedMetaData[0], qualifiedMetaData[1]);
                        if (qualifiedMetaData.length > 2)
                            record.putMetaData(qualifiedMetaData[2], qualifiedMetaData[3]);
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
