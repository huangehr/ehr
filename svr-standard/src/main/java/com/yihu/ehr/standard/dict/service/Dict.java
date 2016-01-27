package com.yihu.ehr.standard.dict.service;

import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.standardsource.service.StandardSource;

import java.util.Date;
import java.util.Objects;

/**
 * 字典。
 * <p>
 * 字典除了基本的作者，创建日期，名称，来源等属性，还有一个特殊的“基础字典”属性，用于方便各地方进行扩展。如：国家字典包含的内容非常少，这时候各医疗机构或厂商可能
 * 对国家字典进行扩展，因此有必要对字典设置父属内容。
 *
 * @version 1.0
 * @created 14-7月-2015 16:33:51
 */
public class Dict{

    long id;
    String code;//字典代码
    String name;//字典名称
    String author;// 作者
    long baseDict;//参考/继承的字典
    Date createdate;//创建日期
    String description;//字典描述
    String sourceId;
    StandardSource source;
    String stdVersion;// 标准化版本
    int hashCode;
    String OperationType;
    String innerVersion;
    int inStage;

    public Dict() {
        this.OperationType = "";
    }


    public long getId() {
        return id;
    }
    public void setId(long dictid) {
        this.id = dictid;
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


    public long getBaseDictId() {
        return baseDict;
    }
    public void setBaseDictId(Long baseDictId) {
        if(baseDictId==null)
            this.baseDict = 0;
        else
            this.baseDict = baseDictId;
    }


    public Date getCreateDate() {
        return createdate;
    }
    public void setCreateDate(Date createDate) {
        this.createdate = createDate;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public String getInnerVersion() {
        return innerVersion;
    }
    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }
    public void setInnerVersion(CDAVersion innerVersion) {
        this.innerVersion = innerVersion.getVersion();
    }

    public StandardSource getSource() {
        return source;
    }
    public void setSource(StandardSource source) {
        this.source = source;
    }


    public String getStdVersion() {
        return stdVersion;
    }
    public void setStdVersion(String version) {
        this.stdVersion = version;
    }


    public int getHashCode() {
        hashCode = Objects.hash(stdVersion, code, name, inStage, description, baseDict, author, createdate);
        return hashCode;
    }
    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }


    public int getInStage() {
        return inStage;
    }
    public boolean isInStage() {
        return false;
    }
    public void setInStage(int inStage) {
        this.inStage = inStage;
    }


    public String getOperationType() {
        return OperationType;
    }
    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }


//    public void setInnerVersion(CDAVersion innerVersion) {
//        this.innerVersion = innerVersion.getVersion();
//    }



//    public XDictEntry getDictEntry(String code) {
//        return dictManager.getDictEntries(this,code);
//    }

//    public XDictEntry[] getDictEntries(List<Integer> ids) {
//        return dictManager.getDictEntries(this, ids);
//    }

//    public XDictEntry[] getDictEntries() {
//        return dictManager.getDictEntries(this);
//    }

//    public CDAVersion getInnerVersion() {
//        XCDAVersionManager cdaVersionManager = ServiceFactory.getService(Services.CDAVersionManager);
//        return cdaVersionManager.getVersion(innerVersion);
//    }
}