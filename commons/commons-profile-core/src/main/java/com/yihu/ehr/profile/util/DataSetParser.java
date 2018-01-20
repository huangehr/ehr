package com.yihu.ehr.profile.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
     * 结构化档案包数据集处理
     * @param jsonNode
     * @param isOrigin
     * @return
     */
    public PackageDataSet parseStructuredJsonDataSet(JsonNode jsonNode, boolean isOrigin) {
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
        if(StringUtils.isEmpty(code)){
            errorMsg.append("dataSetCode is null;");
        }
        if(StringUtils.isEmpty(patientId)){
            errorMsg.append("patientId is null;");
        }
        if(StringUtils.isEmpty(eventNo) && !"HDSA00_01".equals(code)){
            errorMsg.append("eventNo is null;");
        }
        if(StringUtils.isEmpty(orgCode)){
            errorMsg.append("orgCode is null;");
        }
        if(!StringUtils.isEmpty(errorMsg.toString())){
            throw new RuntimeException(errorMsg.toString());
        }

        try {
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(version);
            dataSet.setCode(code);
            dataSet.setOrgCode(orgCode);
            dataSet.setEventTime(DateUtil.strToDate(eventTime));
            dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));
            dataSet.setReUploadFlg(reUploadFlg);
            JsonNode dataNode = jsonNode.get("data");
            for (int i = 0; i < dataNode.size(); i ++) {
                MetaDataRecord record = new MetaDataRecord();
                JsonNode recordNode = dataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaDataKey = item.getKey();
                    //if (metaDataKey.equals("EVENT_NO")) continue;
                    // metaData.equals("PATIENT_ID") ||
                    /*String[] qualifiedMetaData = translateMetaData(
                            version, dataSetCode, metaData,
                            item.getValue().asText().equals("null") ? "" : item.getValue().asText(),
                            isOrigin);

                    if(qualifiedMetaData != null){
                        record.putMetaData(qualifiedMetaData[0], qualifiedMetaData[1]);
                        if (qualifiedMetaData.length > 2)
                            record.putMetaData(qualifiedMetaData[2], qualifiedMetaData[3]);
                    }*/
                    String metaDataValue = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                    record.putMetaData(metaDataKey, metaDataValue);
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
     * 结构化 - 即时交互档案数据集处理
     * @param jsonNode
     * @return
     */
    public List<PackageDataSet> parseStructuredImmediateJson(JsonNode jsonNode) {
        List<PackageDataSet> packageDataSetList = new ArrayList<>();

        //获取就诊事件索引信息
        String patientId = jsonNode.get("patient_id") == null ? "" : jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no") == null ? "" : jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code") == null ? "" : jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version") == null ? "" : jsonNode.get("inner_version").asText();
        String createTime = jsonNode.get("create_date") == null ? "" : jsonNode.get("create_date").asText();
        String eventTime = jsonNode.path("event_time") == null ? "" : jsonNode.path("event_time").asText();
        boolean reUploadFlg = jsonNode.path("reUploadFlg") == null ? false : jsonNode.path("reUploadFlg").asBoolean();

        //验证档案基础数据的完整性，当其中某字段为空的情况下直接提示档案信息缺失。
        StringBuilder errorMsg = new StringBuilder();
        if(StringUtils.isEmpty(patientId)){
            errorMsg.append("patientId is null;");
        }
        if(StringUtils.isEmpty(eventNo) ){
            errorMsg.append("eventNo is null;");
        }
        if(StringUtils.isEmpty(orgCode)){
            errorMsg.append("orgCode is null;");
        }

        if(jsonNode.get("data")==null){
            errorMsg.append("dataSets is null;");
        }

        if(!StringUtils.isEmpty(errorMsg.toString())){
            throw new RuntimeException(errorMsg.toString());
        }

        //获取档案中数据集json数据
        JsonNode dataNode = jsonNode.get("data");
        Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();

        while (fields.hasNext()){
            //遍历标准数据集数据
            Map.Entry<String, JsonNode> next = fields.next();
            String dataSetCode = next.getKey();
            ArrayNode dataSets = (ArrayNode) next.getValue();

            PackageDataSet dataSet = new PackageDataSet();
            try {
                dataSet.setPatientId(patientId);
                dataSet.setEventNo(eventNo);
                dataSet.setCdaVersion(version);
                dataSet.setCode(dataSetCode);
                dataSet.setOrgCode(orgCode);
                dataSet.setEventTime(DateUtil.strToDate(eventTime));
                dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));
                dataSet.setReUploadFlg(reUploadFlg);

                for (int i = 0; i < dataSets.size(); i ++) {
                    MetaDataRecord record = new MetaDataRecord();
                    JsonNode recordNode = dataSets.get(i);
                    Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> item = iterator.next();
                        String metaDataKey = item.getKey();
                        //if (metaDataKey.equals("EVENT_NO")) continue;
                        // metaData.equals("PATIENT_ID") ||
                    /*String[] qualifiedMetaData = translateMetaData(
                            version, dataSetCode, metaData,
                            item.getValue().asText().equals("null") ? "" : item.getValue().asText(),
                            isOrigin);

                    if(qualifiedMetaData != null){
                        record.putMetaData(qualifiedMetaData[0], qualifiedMetaData[1]);
                        if (qualifiedMetaData.length > 2)
                            record.putMetaData(qualifiedMetaData[2], qualifiedMetaData[3]);
                    }*/
                        String metaDataValue = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        record.putMetaData(metaDataKey, metaDataValue);
                    }
                    dataSet.addRecord(Integer.toString(i), record);
                }
                packageDataSetList.add(dataSet);
            } catch (NullPointerException e) {
                throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
            } catch (ParseException e) {
                throw new RuntimeException("Invalid date time format.");
            }

        }



        return packageDataSetList;
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
