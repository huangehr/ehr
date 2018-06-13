package com.yihu.ehr.profile.qualilty;

/**
 * @author yeshijie on 2018/6/12.
 */
public enum DqWarningRecordType {
    receive("平台接收", "1"),
    resource("资源化", "2"),
    upload("平台上传", "3");
    private String name;
    private String value;

    DqWarningRecordType(String name, String value) {
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
