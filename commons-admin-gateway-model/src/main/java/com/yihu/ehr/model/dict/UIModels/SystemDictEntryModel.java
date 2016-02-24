package com.yihu.ehr.model.dict.UIModels;

/**
 * Created by wq on 2016/2/19.
 */
public class SystemDictEntryModel {

    private String id;

    private String code;

    private String value;

    private String sort;

    private String catalog;

    private String phoneticCode;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPhoneticCode() {
        return phoneticCode;
    }

    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }
}
