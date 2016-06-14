package com.yihu.ehr.model.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 档案数据集。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class MDataSet {
    private String code;
    private String name;

    private Map<String, MRecord> records = new HashMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String dataSetCode) {
        this.code = dataSetCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, MRecord> getRecords() {
        return this.records;
    }

    public void setRecords(Map<String, MRecord> records) {
        this.records = records;
    }

    public String toString(){
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ObjectNode objectNode = objectMapper.createObjectNode();
        for (String key : records.keySet()){
            ObjectNode cells = objectNode.putObject(key);

            MRecord record = records.get(key);
            for (String cell : record.getCells().keySet()){
                cells.put(cell, record.getCells().get(cell));
            }
        }

        return objectNode.toString();
    }
}
