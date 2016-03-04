package com.yihu.ehr.agModel.standard.dict;

/**
 * Created by AndyCai on 2016/2/29.
 */
public class DictModel {

    long id;

    String code;//字典代码

    String name;//字典名称

    String author;// 作者

    Long baseDict;//参考/继承的字典

    String description;//字典描述

    String sourceId;

    String stdVersion;// 标准化版本

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getBaseDict() {
        return baseDict;
    }

    public void setBaseDict(Long baseDict) {
        this.baseDict = baseDict;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getStdVersion() {
        return stdVersion;
    }

    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
    }
}
