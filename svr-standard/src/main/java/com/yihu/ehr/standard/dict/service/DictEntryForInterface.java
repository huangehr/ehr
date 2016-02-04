package com.yihu.ehr.standard.dict.service;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class DictEntryForInterface {
    String code;
    String dictId;
    String value;
    String stdVersion;
    String id;
    String desc;
    String hashCode;


    public DictEntryForInterface() {
        this.OperationType = "";
    }

    public void finalize() throws Throwable {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getStdVersion() {
        return stdVersion;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    String OperationType;
}
