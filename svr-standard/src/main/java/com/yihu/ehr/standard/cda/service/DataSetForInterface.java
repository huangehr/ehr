package com.yihu.ehr.standard.cda.service;

/**
 * 数据集实现类. ID与版本联合起来表示一个主键。此数据集用于表示一个已经版本好的对象。
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:03
 */
public class DataSetForInterface {
    public String id;

    String documentId;
    String catalog;

    String hashCode;
    String lang;
    String reference;
    String publisher;
    String stdVersion;
    String code;
    String name;
    String summary;
    String innerVersionId;

    String OperationType;

    public DataSetForInterface(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
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

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }
}
