package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ProfileType;
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
 * @created 2015.08.16 10:44
 */
public class StructedProfile extends Profile {
    private Map<String, StdDataSet> dataSets = new HashMap<>();

    public StructedProfile(){
        this.setProfileType(ProfileType.Structured);
    }

    public void insertDataSet(String dataSetCode, StdDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public StdDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }

    public Collection<StdDataSet> getDataSets() {
        return dataSets.values();
    }

    public String getDataSetIndices() {
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String dataSetCode : dataSets.keySet()) {
            Set<String> recordKeys = dataSets.get(dataSetCode).getRecordKeys();
            rootNode.put(dataSetCode, String.join(",", recordKeys));
        }

        return rootNode.toString();
    }

    public String toJson(){
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
            StdDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.addPOJO(dataSet.toJson());
        }

        return root.toString();
    }
}
