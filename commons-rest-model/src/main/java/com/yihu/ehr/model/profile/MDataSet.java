package com.yihu.ehr.model.profile;

import java.util.Map;
import java.util.TreeMap;

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

    private Map<String, MRecord> records = new TreeMap<>();

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
}
