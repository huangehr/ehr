package com.yihu.ehr.profile.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
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
public class DataSetParserUtil {

    /**
     * 结构化档案包数据集处理
     * @param jsonNode
     * @param isOrigin
     * @return
     */
    public PackageDataSet parseStructuredJsonDataSet(JsonNode jsonNode, boolean isOrigin) {
        PackageDataSet dataSet = new PackageDataSet();

        String code = jsonNode.get("code") == null ? "" : jsonNode.get("code").asText();
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
            errorMsg.append("code is null;");
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
        if (StringUtils.isEmpty(version)) {
            errorMsg.append("innerVersion is null;");
        }
        if (StringUtils.isEmpty(eventTime)) {
            errorMsg.append("eventTime is null;");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())){
            throw new IllegalJsonDataException(errorMsg.toString());
        }
        dataSet.setCode(code);
        dataSet.setPatientId(patientId);
        dataSet.setEventNo(eventNo);
        dataSet.setOrgCode(orgCode);
        dataSet.setCdaVersion(version);
        dataSet.setCreateTime(DateUtil.strToDate(createTime));
        dataSet.setEventTime(DateUtil.strToDate(eventTime));
        dataSet.setReUploadFlg(reUploadFlg);
        JsonNode dataNode = jsonNode.get("data");
        for (int i = 0; i < dataNode.size(); i ++) {
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
