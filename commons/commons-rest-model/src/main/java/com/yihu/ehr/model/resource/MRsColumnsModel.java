package com.yihu.ehr.model.resource;

import java.io.Serializable;
import java.util.List;

/**
 * Created by progr1mmer on 2018/4/16.
 */
public class MRsColumnsModel implements Serializable {
    private String code;
    private String value;
    private String type;
    private String dict;

    private List<String> columnsCode;
    private List<String> columnsName;
    private List<String> columnsType;
    private List<String> columnsDict;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }

    public List<String> getColumnsCode() {
        return columnsCode;
    }

    public void setColumnsCode(List<String> columnsCode) {
        this.columnsCode = columnsCode;
    }

    public List<String> getColumnsName() {
        return columnsName;
    }

    public void setColumnsName(List<String> columnsName) {
        this.columnsName = columnsName;
    }

    public List<String> getColumnsType() {
        return columnsType;
    }

    public void setColumnsType(List<String> columnsType) {
        this.columnsType = columnsType;
    }

    public List<String> getColumnsDict() {
        return columnsDict;
    }

    public void setColumnsDict(List<String> columnsDict) {
        this.columnsDict = columnsDict;
    }
}
