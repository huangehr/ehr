package com.yihu.ehr.model.standard;

/**
 * 数据集
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
public class MStdDataSet {
    private long id;
    private long documentId;
    private int catalog;
    private int hashCode;
    private int lang;
    private int publisher;
    private boolean multiRecord;
    private String reference;
    private String stdVersion;
    private String code;
    private String name;
    private String summary;
    private String innerVersionId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public String getStdVersion() {
        return stdVersion;
    }

    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInnerVersionId() {
        return innerVersionId;
    }

    public void setInnerVersionId(String innerVersionId) {
        this.innerVersionId = innerVersionId;
    }

    public boolean isMultiRecord() {
        return multiRecord;
    }

    public void setMultiRecord(boolean multiRecord) {
        this.multiRecord = multiRecord;
    }
}