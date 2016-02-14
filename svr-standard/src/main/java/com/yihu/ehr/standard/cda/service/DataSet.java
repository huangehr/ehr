package com.yihu.ehr.standard.cda.service;
import org.apache.naming.factory.BeanFactory;

import java.util.Objects;

/**
 * 数据集实现类. ID与版本联合起来表示一个主键。此数据集用于表示一个已经版本好的对象。
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:03
 */
public class DataSet  {
    public long id;

    long documentId;
    int catalog;

    int hashCode;
    int lang;
    String reference;
    int publisher;
    String stdVersion;
    String code;
    String name;
    String summary;

    String innerVersionId;

    String OperationType;


    public DataSet(){
        this.OperationType="";
    }


    public int getCatalog() {
        return catalog;
    }


    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public long getDocumentId() {
        return documentId;
    }


    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }


    public int getHashCode() {
        hashCode = Objects.hash(documentId, catalog, lang, reference,
                publisher, stdVersion, code, name, summary);

        return hashCode;
    }

    public void setHashCode(int hashCode){
        this.hashCode = hashCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CDAVersion getInnerVersion() {
        //// TODO: 2016/2/5
        CDAVersionManager versionManager = new CDAVersionManager();
        return versionManager.getVersion(innerVersionId);
    }

    public void setInnerVersion(CDAVersion innerVersion) {
        this.innerVersionId = innerVersion.getVersion();
    }

    public String getInnerVersionId() {
        return innerVersionId;
    }

    public void setInnerVersionId(String innerVersionId) {
        this.innerVersionId = innerVersionId;
    }


    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getPublisher() {
        return publisher;
    }


    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }


    public String getReference() {
        return reference;
    }


    public void setReference(String reference) {
        this.reference = reference;
    }


    public String getStdVersion() {
        return stdVersion;
    }


    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
    }


    public String getSummary() {
        return summary;
    }


    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }


//    public MetaData createMetaData(){
//        MetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        return metaDataManager.createMetaData(this);
//    }
//
//
//    public MetaData getMetaData(long metaDataId){
//        MetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        return metaDataManager.getMetaData(this, metaDataId);
//    }
//
//
//    public List<MetaData> getMetaDataList() {
//        MetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        return metaDataManager.getMetaDataList(this);
//    }
//
//
//    public List<MetaData> getMetaDataList(List<Integer> ids){
//        MetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        return metaDataManager.getMetaDataList(this, ids);
//    }
//
//
//    public void saveMetaData(MetaData metaData){
//        MetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        metaDataManager.saveMetaData(this, metaData);
//    }
//
//
//    public void removeMetaData(long metaDataId){
//        MetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        metaDataManager.removeMetaData(innerVersionId, metaDataId);
//    }
}