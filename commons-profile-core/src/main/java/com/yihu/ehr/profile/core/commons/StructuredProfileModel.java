package com.yihu.ehr.profile.core.commons;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.core.lightweight.LightWeightDataSet;
import com.yihu.ehr.profile.core.structured.FullWeightDataSet;

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
public class StructuredProfileModel extends Profile {

    private ProfileType profileType;                         // 档案类型

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    // 档案包含的数据集, key 为数据集的表名, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    public Map<String, FullWeightDataSet> fullWeightDataSets = new HashMap<>();

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

    public void FullWeightDataSet(String dataSetCode, FullWeightDataSet dataSet) {
        this.fullWeightDataSets.put(dataSetCode, dataSet);
    }

    public void removeFullWeightData(String dataSetCode){
        this.fullWeightDataSets.remove(dataSetCode);
    }

    public FullWeightDataSet getFullWeightData(String dataSetCode) {
        return this.fullWeightDataSets.get(dataSetCode);
    }

    public Set<String> getFullWeightDataTables(){
        return this.fullWeightDataSets.keySet();
    }

    public void addFullWeightDataSet(String dataSetCode, FullWeightDataSet fullWeightDataSet) {
        this.fullWeightDataSets.put(dataSetCode, fullWeightDataSet);
    }





    private Map<String, LightWeightDataSet> lightWeightDataSets = new HashMap<>();

    public Collection<LightWeightDataSet> getLightWeightDataSets() {
        return lightWeightDataSets.values();
    }

    public String getLightWeightDataSetsAsString() {
        ObjectNode rootNode = objectMapper.createObjectNode();
        for (String key : lightWeightDataSets.keySet()) {
            Set<String> rowKeys = lightWeightDataSets.get(key).getRecordKeys();
            String records = String.join(",", rowKeys);
            rootNode.put(key, records);
        }
        return rootNode.toString();
    }

    public void LightWeightDataSet(String dataSetCode, LightWeightDataSet dataSet) {
        this.lightWeightDataSets.put(dataSetCode, dataSet);
    }

    public void removeLightWeightDataSet(String dataSetCode){
        this.lightWeightDataSets.remove(dataSetCode);
    }

    public LightWeightDataSet getLightWeightDataSet(String dataSetCode) {
        return this.lightWeightDataSets.get(dataSetCode);
    }

    public Set<String> getLightWeightDataSetTables(){
        return this.lightWeightDataSets.keySet();
    }

    public void addLightWeightDataSet(String dataSetCode, LightWeightDataSet lightWeightDataSet) {
        this.lightWeightDataSets.put(dataSetCode, lightWeightDataSet);
    }




}
