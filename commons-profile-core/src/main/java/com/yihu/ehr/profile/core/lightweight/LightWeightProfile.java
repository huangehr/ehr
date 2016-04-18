package com.yihu.ehr.profile.core.lightweight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.commons.DataSet;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.util.DateFormatter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class LightWeightProfile extends Profile {

    private ObjectMapper objectMapper = SpringContext.getService("objectMapper");
    // 档案包含的数据集, key 为数据集的表名, 标准数据情况下, 表名为数据集代码, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    private Map<String, LightWeightDataSet> lightWeightDataSets = new HashMap<>();;


    public Collection<LightWeightDataSet> getDataSets() {
        return lightWeightDataSets.values();
    }


    public String getDataSetsAsString() {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String key : lightWeightDataSets.keySet()) {
            Set<String> rowKeys = lightWeightDataSets.get(key).getRecordKeys();
            String records = String.join(",", rowKeys);
            rootNode.put(key, records);
        }

        return rootNode.toString();
    }

    public void addDataSet(String dataSetCode, LightWeightDataSet lightWeightDataSets) {
        this.lightWeightDataSets.put(dataSetCode, lightWeightDataSets);
    }

    public void removeDataSet(String dataSetCode){
        this.lightWeightDataSets.remove(dataSetCode);
    }

    public LightWeightDataSet getDataSet(String dataSetCode) {
        return this.lightWeightDataSets.get(dataSetCode);
    }



    public Set<String> getDataSetTables(){
        return this.lightWeightDataSets.keySet();
    }


    public String toJson(){
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("card_id", this.getCardId());
        root.put("org_code", this.getOrgCode());
        root.put("org_name", this.getOrgName());
        root.put("patient_id", this.getPatientId());
        root.put("event_no", this.getEventNo());
        root.put("event_date", this.getEventDate() == null ? "" : DateFormatter.utcDateTimeFormat(this.getEventDate()));
        root.put("cda_version", this.getCdaVersion());
        root.put("create_date", this.getCreateDate() == null ? "" : DateFormatter.utcDateTimeFormat(this.getCreateDate()));
        root.put("summary", this.getSummary());

        ArrayNode dataSetsNode = root.putArray("data_sets");
        for (String dataSetCode : lightWeightDataSets.keySet()){
            DataSet dataSet = lightWeightDataSets.get(dataSetCode);
            dataSetsNode.addPOJO(dataSet.toJson(false));
        }

        return root.toString();
    }

}
