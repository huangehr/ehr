package com.yihu.ehr.model.resource;

/**
 * @author linaz
 * @created 2016.05.30 13:48
 */
public class MRsAdapterDictionary {

    private String id;
    private String schemeId;
    private String dictCode;
    private String dictEntryCode;
    private String srcDictCode;
    private String srcDictEntryCode;
    private String srcDictEntryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictEntryCode() {
        return dictEntryCode;
    }

    public void setDictEntryCode(String dictEntryCode) {
        this.dictEntryCode = dictEntryCode;
    }

    public String getSrcDictCode() {
        return srcDictCode;
    }

    public void setSrcDictCode(String srcDictCode) {
        this.srcDictCode = srcDictCode;
    }

    public String getSrcDictEntryCode() {
        return srcDictEntryCode;
    }

    public void setSrcDictEntryCode(String srcDictEntryCode) {
        this.srcDictEntryCode = srcDictEntryCode;
    }

    public String getSrcDictEntryName() {
        return srcDictEntryName;
    }

    public void setSrcDictEntryName(String srcDictEntryName) {
        this.srcDictEntryName = srcDictEntryName;
    }
}