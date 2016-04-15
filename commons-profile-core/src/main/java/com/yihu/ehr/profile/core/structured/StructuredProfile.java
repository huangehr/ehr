package com.yihu.ehr.profile.core.structured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.commons.Profile;

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
public class StructuredProfile extends Profile {
    // 档案包含的数据集, key 为数据集的表名, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    private Map<String, StructuredDataSet> dataSets = new HashMap<>();

    public Collection<StructuredDataSet> getDataSets() {
        return dataSets.values();
    }

    public String getDataSetsAsString() {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
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
}
