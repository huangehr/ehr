package com.yihu.ehr.profile.core.lightweight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.commons.Profile;

import java.util.Collection;
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


    // 档案包含的数据集, key 为数据集的表名, 标准数据情况下, 表名为数据集代码, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    private Map<String, LightWeightDataSet> lightWeightDataSets;



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

    public void addDataSet(String dataSetCode, LightWeightDataSet dataSet) {
        this.lightWeightDataSets.put(dataSetCode, dataSet);
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
