package com.yihu.ehr.profile.core.structured;

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
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class FullWeightProfile extends Profile {

    private ProfileType profileType;                         // 档案类型


    // 档案包含的数据集, key 为数据集的表名, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    private Map<String, FullWeightDataSet> fullWeightDataSets = new HashMap<>();


    public ProfileType getProfileType() {
        return ProfileType.FullWeight;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public Collection<FullWeightDataSet> getFullWeightDataSets() {
        return fullWeightDataSets.values();
    }

    public String getFullWeightDataSetsAsString() {
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String key : fullWeightDataSets.keySet()) {
            Set<String> rowKeys = fullWeightDataSets.get(key).getRecordKeys();
            String records = String.join(",", rowKeys);
            rootNode.put(key, records);
        }

        return rootNode.toString();
    }

    public void addFullWeightDataSet(String dataSetCode, FullWeightDataSet dataSet) {
        this.fullWeightDataSets.put(dataSetCode, dataSet);
    }

    public void removeFullWeightDataSet(String dataSetCode){
        this.fullWeightDataSets.remove(dataSetCode);
    }

    public FullWeightDataSet getFullWeightDataSet(String dataSetCode) {
        return this.fullWeightDataSets.get(dataSetCode);
    }

    public Set<String> getFullWeightDataSetTables(){
        return this.fullWeightDataSets.keySet();
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
        for (String dataSetCode : fullWeightDataSets.keySet()){
            FullWeightDataSet fullWeightDataSet = fullWeightDataSets.get(dataSetCode);
            dataSetsNode.addPOJO(fullWeightDataSet.toJson(false));
        }

        return root.toString();
    }
}
