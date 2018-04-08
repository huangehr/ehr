package com.yihu.ehr.model.dict;

import com.yihu.ehr.model.app.MApp;

import java.util.List;

/**
 * Created by progr1mmer on 2018/4/8.
 */
public class SystemDictEntryAppModel {
    private long dictId;

    private String code;

    private String value;

    private Integer sort;

    private String catalog;

    private String phoneticCode;

    private List<MApp> children;


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

    public List<MApp> getChildren() {
        return children;
    }

    public void setChildren(List<MApp> children) {
        this.children = children;
    }
}
