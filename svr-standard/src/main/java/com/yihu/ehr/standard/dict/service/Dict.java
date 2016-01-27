package com.yihu.ehr.standard.dict.service;

import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.standardsource.service.StandardSource;

import java.util.Date;
import java.util.Objects;

/**
 * �ֵ䡣
 * <p>
 * �ֵ���˻��������ߣ��������ڣ����ƣ���Դ�����ԣ�����һ������ġ������ֵ䡱���ԣ����ڷ�����ط�������չ���磺�����ֵ���������ݷǳ��٣���ʱ���ҽ�ƻ������̿���
 * �Թ����ֵ������չ������б�Ҫ���ֵ����ø������ݡ�
 *
 * @version 1.0
 * @created 14-7��-2015 16:33:51
 */
public class Dict{

    long id;
    String code;//�ֵ����
    String name;//�ֵ�����
    String author;// ����
    long baseDict;//�ο�/�̳е��ֵ�
    Date createdate;//��������
    String description;//�ֵ�����
    String sourceId;
    StandardSource source;
    String stdVersion;// ��׼���汾
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