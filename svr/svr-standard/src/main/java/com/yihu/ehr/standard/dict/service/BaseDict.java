package com.yihu.ehr.standard.dict.service;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 字典。
 * <p/>
 * 字典除了基本的作者，创建日期，名称，来源等属性，还有一个特殊的“基础字典”属性，用于方便各地方进行扩展。如：国家字典包含的内容非常少，这时候各医疗机构或厂商可能
 * 对国家字典进行扩展，因此有必要对字典设置父属内容。
 *
 * @version 1.0
 * @created 14-7月-2015 16:33:51
 */
@MappedSuperclass
public class BaseDict {
    long id;
    String code;//字典代码
    String name;//字典名称
    String author;// 作者
    Long baseDict;//参考/继承的字典
    Date createDate;//创建日期
    String description;//字典描述
    String sourceId;
    String stdVersion;// 标准化版本
    int hashCode;
    String innerVersion;
    int inStage;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "code", unique = false, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", unique = false, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "author", unique = false, nullable = true)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "base_dict", unique = false, nullable = true)
    public Long getBaseDict() {
        return baseDict;
    }

    public void setBaseDict(Long baseDict) {
        this.baseDict = baseDict;
    }

    @Column(name = "create_date", unique = false, nullable = true)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "description", unique = false, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "source", unique = false, nullable = true)
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "std_version", unique = false, nullable = true)
    public String getStdVersion() {
        return stdVersion;
    }

    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
    }

    @Column(name = "hash", unique = false, nullable = true)
    public int getHashCode() {
        hashCode = Objects.hash(stdVersion, code, name, inStage, description, baseDict, author, createDate);
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Transient
    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    @Transient
    public int getInStage() {
        return inStage;
    }

    public void setInStage(int inStage) {
        this.inStage = inStage;
    }
}