package com.yihu.ehr.profile.qualilty;

/**
 * @author yeshijie on 2018/6/12.
 */
public enum DqWarningRecordWarningType {
    archives("档案数", "101"),
    errorNum("质量异常问题数", "102"),
    datasetWarningNum("数据集", "103"),
    outpatientInTimeRate("门诊及时率", "104"),
    hospitalInTimeRate("住院及时率", "105"),
    peInTimeRate("体检及时率", "106"),
    resourceFailureNum("解析失败数", "201"),
    resourceErrorNum("解析质量问题数", "202"),
    unArchiveNum("未解析量", "203"),
    archiveNum("档案数", "301"),
    dataErrorNum("数据错误问题数", "302"),
    uploadDatasetNum("数据集", "303");
    private String name;
    private String value;

    DqWarningRecordWarningType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
