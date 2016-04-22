package com.yihu.ehr.profile.core.lightweight;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.core.commons.Profile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 轻量级健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class LightWeightProfile extends Profile {

    private Map<String, LightWeightDataSet> lightWeightDataSets = new HashMap<>();;

    public Collection<LightWeightDataSet> getDataSets() {
        return lightWeightDataSets.values();
    }

    public String getDataSetsAsString() {
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
}
