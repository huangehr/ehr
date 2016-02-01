package com.yihu.ehr.model.dict;

import java.io.Serializable;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public class MBaseDict implements Serializable {
    private static final long serialVersionUID = 1L;

    String code;
    long dictId;
    String value;
    Integer sort;
    String phoneticCode;
    String catalog;
    public MBaseDict(){
    }

    public boolean checkIsVirtualCard(){
        if(this.getCatalog().equals("VirtualCard")){
            return true;
        }else{
            return false;
        }
    }


    public MBaseDict(long dictId, String code, String value, Integer sort, String phoneticCode, String catalog){
        this.dictId = dictId;
        this.code = code;
        this.value = value;
        this.sort = sort;
        this.phoneticCode = phoneticCode;
        this.catalog = catalog;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public long getDictId() {
        return dictId;
    }
    public void setDictId(long dictId) {
        this.dictId = dictId;
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

    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
