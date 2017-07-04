package com.yihu.ehr.agModel.dict;

import com.yihu.ehr.agModel.app.AppModel;

import java.util.List;

/**
 * Created by wq on 2016/2/19.
 */
public class SystemDictEntryModel {

    private long dictId;

    private String code;

    private String value;

    private Integer sort;

    private String catalog;

    private String phoneticCode;
    private List<AppModel> children;


    public long getDictId() {
        return dictId;
    }

    public void setDictId(long dictId) {
        this.dictId = dictId;
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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
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

    public List<AppModel> getChildren() {
        return children;
    }

    public void setChildren(List<AppModel> children) {
        this.children = children;
    }
}
