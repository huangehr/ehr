package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.DateFormatter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 结构化档案。增加档案数据集结构。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 14:52
 */
public class StructedProfile extends Profile {
    // 档案数据集, key 为数据集代码, 原始数据集使用"数据集代码_ORIGIN"标识
    protected Map<String, ProfileDataSet> dataSets = new TreeMap<>();

    public StructedProfile() {
    }

    public Collection<ProfileDataSet> getDataSets() {
        return dataSets.values();
    }

    public void addDataSet(String dataSetCode, ProfileDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public void removeDataSet(String dataSetCode){
        this.dataSets.remove(dataSetCode);
    }

    public ProfileDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }

    public Set<String> getDataSetTables(){
        return this.dataSets.keySet();
    }

    public ProfileType getType(){
        return ProfileType.Structed;
    }

    public String getDataSetRowKeyList() {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String key : dataSets.keySet()) {
            Set<String> rowKeys = dataSets.get(key).getRecordKeys();
            String records = String.join(",", rowKeys);
            rootNode.put(key, records);
        }

        return rootNode.toString();
    }

    public String toJson(){
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("card_id", cardId);
        root.put("org_code", orgCode);
        root.put("org_name", orgName);
        root.put("patient_id", patientId);
        root.put("event_no", eventNo);
        root.put("event_date", eventDate == null ? "" : DateFormatter.utcDateTimeFormat(eventDate));
        root.put("cda_version", cdaVersion);
        root.put("create_date", createDate == null ? "" : DateFormatter.utcDateTimeFormat(createDate));
        root.put("summary", summary);

        ArrayNode dataSetsNode = root.putArray("data_sets");
        for (String dataSetCode : dataSets.keySet()){
            ProfileDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.addPOJO(dataSet.toJson(false));
        }

        return root.toString();
    }
}
