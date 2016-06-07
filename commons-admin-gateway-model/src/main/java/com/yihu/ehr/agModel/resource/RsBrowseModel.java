package com.yihu.ehr.agModel.resource;

import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * Created by wq on 2016/6/3.
 */
public class RsBrowseModel {
    private String code;
    private String value;
    private String type;
    private String dict;

    private List<String> colunmCode;
    private List<String> colunmName;
    private List<String> colunmType;
    private List<String> colunmDict;

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

    public List<String> getColunmCode() {
        return colunmCode;
    }

    public void setColunmCode(List<String> colunmCode) {
        this.colunmCode = colunmCode;
    }

    public List<String> getColunmName() {
        return colunmName;
    }

    public void setColunmName(List<String> colunmName) {
        this.colunmName = colunmName;
    }

    public List<String> getColunmType() {
        return colunmType;
    }

    public void setColunmType(List<String> colunmType) {
        this.colunmType = colunmType;
    }

    public List<String> getColunmDict() {
        return colunmDict;
    }

    public void setColunmDict(List<String> colunmDict) {
        this.colunmDict = colunmDict;
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

    public RsBrowseModel() {
    }

    public RsBrowseModel(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
