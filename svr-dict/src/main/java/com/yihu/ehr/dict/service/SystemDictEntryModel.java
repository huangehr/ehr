package com.yihu.ehr.dict.service;

/**
 * Created by Administrator on 2015/8/16.
 */
public class SystemDictEntryModel {

    String id;
    String code;
    String value;
    String sort;
    String catalog;
    String phoneticCode;

    public String getCatalog() {return catalog;}

    public void setCatalog(String catalog) {this.catalog = catalog;}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneticCode() {
        return phoneticCode;
    }

    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
