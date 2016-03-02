package com.yihu.ehr.model.standard;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 字典数据模型
 *
 * @version 1.0
 * @created 2016.2.22
 */
@MappedSuperclass
public class MStdDict {
    long id;
    String code;//字典代码
    String name;//字典名称
    String author;// 作者
    long baseDict;//参考/继承的字典
    Date createdate;//创建日期
    String description;//字典描述
    String sourceId;
    String stdVersion;// 标准化版本
    int hashCode;
    String OperationType;
    String innerVersion;
    int inStage;

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

    public long getBaseDict() {
        return baseDict;
    }

    public void setBaseDict(long baseDict) {
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

    public int getHashCode() {
        hashCode = Objects.hash(stdVersion, code, name, inStage, description, baseDict, author, createdate);
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    public int getInStage() {
        return inStage;
    }

    public void setInStage(int inStage) {
        this.inStage = inStage;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }
}