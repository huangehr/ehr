package com.yihu.ehr.model.standard;


import java.util.Objects;

/**
 * 字典项数据模型
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
public class MStdDictEntry {

    long id;
    long dictId;//字典ID
    String code;
    String value;
    String desc;
    int hashCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        if (dictId == null)
            this.dictId = 0;
        else
            this.dictId = dictId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getHashCode() {
        hashCode = Objects.hash(dictId, code, value, desc);
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
}