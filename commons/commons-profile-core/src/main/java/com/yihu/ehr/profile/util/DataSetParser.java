package com.yihu.ehr.profile.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.exception.LegacyPackageException;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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
public class DataSetParser {

    /**
     *  TODO 未完整逻辑
     *  add by HZY at 2017/07/03
     * （非档案类型）结构化档案包数据集处理
     *
     * @param root
     * @param isOrigin
     * @return
     */
    public PackageDataSet parseNonArchiveJsonDataSet(JsonNode root, boolean isOrigin) {
        PackageDataSet dataSet = new PackageDataSet();

        JsonNode head = root.get("head");//文件内容头信息
        JsonNode data = root.get("data");//文件内容主体信息

        String version = head.get("version").asText();
        if (version.equals("000000000000")) throw new LegacyPackageException("Package is collected via cda version 00000000000, ignored.");

        String dataSetCode = head.get("target").asText();
        String createTime = head.get("createTime").isNull() ? "" : head.get("createTime").asText();
        String orgCode = head.get("orgCode").asText();

        String eventNo = root.get("event_no").asText();
        String patientId = root.get("patient_id").asText();
        String eventTime = root.path("event_time").isNull() ? "" : root.path("event_time").asText();    // 旧数据集结构可能不存在这个属性

        try {
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(version);
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(orgCode);
            dataSet.setEventTime(DateTimeUtil.simpleDateParse(eventTime));
            dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));

            JsonNode dataNode = root.get("data");
            for (int i = 0; i < dataNode.size(); ++i) {
                MetaDataRecord record = new MetaDataRecord();

                JsonNode recordNode = dataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaData = item.getKey();
                    if (metaData.equals("EVENT_NO")) continue; //metaData.equals("PATIENT_ID") ||

                    /*String[] qualifiedMetaData = translateMetaData(
                            version, dataSetCode, metaData,
                            item.getValue().asText().equals("null") ? "" : item.getValue().asText(),
                            isOrigin);

                    if(qualifiedMetaData != null){
                        record.putMetaData(qualifiedMetaData[0], qualifiedMetaData[1]);
                        if (qualifiedMetaData.length > 2)
                            record.putMetaData(qualifiedMetaData[2], qualifiedMetaData[3]);
                    }*/
                    String value = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                    record.putMetaData(metaData, value);
                }

                dataSet.addRecord(Integer.toString(i), record);
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date time format.");
        }

        return dataSet;
    }

    /**
     * 结构化档案包数据集处理
     *
     * @param root
     * @param isOrigin
     * @return
     */
    public PackageDataSet parseStructuredJsonDataSet(JsonNode root, boolean isOrigin) {
        PackageDataSet dataSet = new PackageDataSet();

        String version = root.get("inner_version").asText();
        if (version.equals("000000000000")) throw new LegacyPackageException("Package is collected via cda version 00000000000, ignored.");

        String dataSetCode = root.get("code").asText();
        String eventNo = root.get("event_no").asText();
        String patientId = root.get("patient_id").asText();
        String orgCode = root.get("org_code").asText();
        String createTime = root.get("create_date").isNull() ? "" : root.get("create_date").asText();
        String eventTime = root.path("event_time").isNull() ? "" : root.path("event_time").asText();    // 旧数据集结构可能不存在这个属性

        try {
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(version);
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(orgCode);
            dataSet.setEventTime(DateTimeUtil.simpleDateParse(eventTime));
            dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));

            JsonNode dataNode = root.get("data");
            for (int i = 0; i < dataNode.size(); ++i) {
                MetaDataRecord record = new MetaDataRecord();

                JsonNode recordNode = dataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaData = item.getKey();
                    if (metaData.equals("EVENT_NO")) continue; //metaData.equals("PATIENT_ID") ||

                    /*String[] qualifiedMetaData = translateMetaData(
                            version, dataSetCode, metaData,
                            item.getValue().asText().equals("null") ? "" : item.getValue().asText(),
                            isOrigin);

                    if(qualifiedMetaData != null){
                        record.putMetaData(qualifiedMetaData[0], qualifiedMetaData[1]);
                        if (qualifiedMetaData.length > 2)
                            record.putMetaData(qualifiedMetaData[2], qualifiedMetaData[3]);
                    }*/
                    String value = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                    record.putMetaData(metaData, value);
                }

                dataSet.addRecord(Integer.toString(i), record);
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date time format.");
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
