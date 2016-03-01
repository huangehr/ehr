package com.yihu.ehr.standard.cda.service;


import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.Objects;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 16:54:17
 */
public class CDADocument {

    @Value("${admin-region}")
    short adminRegion;

    private String code;
    private Date createDate;
    private String createUser;
    private String description;
    private String id;
    private String name;
    private Date updateDate;
    private String updateUser;
    private String versionCode;
    /**
     * 输出排版路径
     */
    private String printOut;
    private String schema;
    /**
     * 标准来源ID
     */
    private String sourceId;
    private int hashCode;
    private String fileGroup;
    private String typeId;


    public CDADocument() {
        this.createDate = new Date();
        this.createUser = "Sys";
        ObjectId objectId = new ObjectId(adminRegion, BizObject.STANDARD);
        id = objectId.toString();
        this.OperationType="";

    }

    public void finalize() throws Throwable {

    }

    public String getPrintOut() {
        return this.printOut;
    }

    public String getSchema() {
        return this.schema;
    }

//    public StandardSource getSource() {
//
//        StandardSourceManager standardSourceManager = ServiceFactory.getService(Services.StandardSourceManager);
//
//        if (this.sourceId == null || this.sourceId .equals(""))
//            return null;
//        List<String> listIds = new ArrayList<>();
//        listIds.add(this.sourceId);
//        StandardSource[] infos = standardSourceManager.getSourceById(listIds);
//        if (infos == null || infos.length<=0)
//            return null;
//        return infos[0];
//    }

    public String getSourceId() {
        return this.sourceId;
    }

    /**
     * @param printOut
     */
    public void setPrintOut(String printOut) {
        this.printOut = printOut;
    }

    /**
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @param sourceId
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getCode() {
        return this.code;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public String getDescription() {
        return this.description;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @param updateUser
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionCode() {
        return this.versionCode;
    }

//    public CDAVersion getVersion() {
//        CDAVersionManager cdaVersionManager=ServiceFactory.getService(Services.CDAVersionManager);
//        if (this.versionCode == null || this.versionCode .equals( ""))
//            return null;
//        return cdaVersionManager.getVersion(this.versionCode);
//    }

    public int getHashCode() {
        hashCode = Objects.hash(id, code, createDate, createUser, name, printOut, schema, sourceId);
        return hashCode;
    }

//    public CdaDatasetRelationship[] getRelationship() {
//        CdaDatasetRelationshipManager xCdaDatasetRelationshipManager = ServiceFactory.getService(Services.DataSetRelationshipManager);
//        if (this.id == null || this.id .equals( "") || this.versionCode == null || this.versionCode.equals( ""))
//            return null;
//        return xCdaDatasetRelationshipManager.getRelationshipByCdaId(this.id, this.versionCode);
//    }

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    String OperationType;

    public String getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(String fileGroup) {
        this.fileGroup = fileGroup;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}//end CDADocument