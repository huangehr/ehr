package com.yihu.ehr.dict.service;


import com.yihu.ehr.dict.service.common.DictPk;
import com.yihu.ehr.util.PinyinUtil;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 字典项。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 15:11
 */
@Entity
@Table(name = "system_dict_entries")
@Access(value = AccessType.PROPERTY)
@Embeddable
@IdClass(DictPk.class)
public class SystemDictEntry  implements Serializable {
    private static final long serialVersionUID = 1L;
    String code;
    long dictId;
    String value;
    Integer sort;
    String phoneticCode;
    String catalog;
    DictPk dictPk;


    public SystemDictEntry(){

    }
    public SystemDictEntry(long dictId, String code, String value, Integer sort, String phoneticCode, String catalog){
        this.dictPk = new DictPk(code,dictId);
        this.value = value;
        this.sort = sort;
        this.phoneticCode = phoneticCode;
        this.catalog = catalog;
    }


    @EmbeddedId
    public DictPk getDictPk() {
        return dictPk;
    }
    public void setDictPk(DictPk dictPk) {
        this.dictPk = dictPk;
    }

    @Column(name = "code", unique = true, nullable = false ,insertable = false, updatable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "dictId", unique = true, nullable = false ,insertable = false, updatable = false)
    public long getDictId() {
        return dictId;
    }
    public void setDictId(long dictId) {
        this.dictId = dictId;
    }

    @Column(name = "catalog", nullable = true)
    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Column(name = "phoneticCode", nullable = true)
    public String getPhoneticCode() {
        return phoneticCode;
    }
    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = PinyinUtil.getPinYinHeadChar(phoneticCode, true);
    }

    @Column(name = "sort", nullable = true)
    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Column(name = "value", nullable = true)
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
