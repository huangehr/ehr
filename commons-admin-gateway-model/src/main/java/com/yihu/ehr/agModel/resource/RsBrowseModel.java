package com.yihu.ehr.agModel.resource;

/**
 * Created by wq on 2016/6/3.
 */
public class RsBrowseModel {
    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RsBrowseModel() {
    }

    public RsBrowseModel(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
