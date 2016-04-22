package com.yihu.ehr.profile.core.structured;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.core.commons.DataSet;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 结构化健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class StructuredProfile extends Profile {
    // 档案包含的数据集, key 为数据集的表名, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    private Map<String, StructuredDataSet> dataSets = new HashMap<>();

    public Collection<StructuredDataSet> getDataSets() {
        return dataSets.values();
    }

    public String getDataSetsAsString() {
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String key : dataSets.keySet()) {
            Set<String> rowKeys = dataSets.get(key).getRecordKeys();
            String records = String.join(",", rowKeys);
            rootNode.put(key, records);
        }

        return rootNode.toString();
    }

    public void addDataSet(String dataSetCode, StructuredDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public void removeDataSet(String dataSetCode){
        this.dataSets.remove(dataSetCode);
    }

    public StructuredDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }

    public Set<String> getDataSetTables(){
        return this.dataSets.keySet();
    }

    public String jsonFormat(){
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("card_id", this.getCardId());
        root.put("org_code", this.getOrgCode());
        root.put("org_name", this.getOrgName());
        root.put("patient_id", this.getPatientId());
        root.put("event_no", this.getEventNo());
        root.put("event_date", this.getEventDate() == null ? "" : DateTimeUtils.utcDateTimeFormat(this.getEventDate()));
        root.put("cda_version", this.getCdaVersion());
        root.put("create_date", this.getCreateDate() == null ? "" : DateTimeUtils.utcDateTimeFormat(this.getCreateDate()));
        root.put("summary", this.getSummary());

        ArrayNode dataSetsNode = root.putArray("data_sets");
        for (String dataSetCode : dataSets.keySet()){
            DataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.addPOJO(dataSet.toJson(false));
        }

        return root.toString();
    }
}
