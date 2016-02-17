//package com.yihu.ehr.standard.standardsource.service;
//
//import com.yihu.ehr.constants.BizObject;
//import com.yihu.ehr.util.ObjectId;
//import org.hibernate.annotations.GenericGenerator;
//import org.springframework.beans.factory.annotation.Value;
//
//import javax.persistence.*;
//import java.util.Date;
//import java.util.Objects;
//
///**
// * @author lincl
// * @version 1.0
// * @created 2016-01-19
// */
//@Entity
//@Table(name = "std_standard_source")
//@Access(value = AccessType.PROPERTY)
//public class StandardSource {
//    @Value("${admin-region}")
//    short adminRegion;
//
//    private String id;
//    private String code;
//    private Date create_date;
//    private String create_user;
//    private String description;
//    private String name;
//    private Date update_date;
//    private String update_user;
//    private String sourceType;
//
//    private String sourceValue;
//    private int hashCode;
//    private String OperationType;
//
//    public StandardSource() {
//        this.create_date = new Date();
//        this.create_user = "Sys";
//        this.OperationType = "";
//
//        Object objectID = new ObjectId(adminRegion, BizObject.StdArchive);
//        this.id = objectID.toString();
//    }
//    @Id
//    @GeneratedValue(generator = "Generator")
//    @GenericGenerator(name = "Generator", strategy = "assigned")
//    @Column(name = "id", unique = true, nullable = false)
//    public String getId() {
//        return this.id;
//    }
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @Column(name = "source_type")
//    public String getSourceType() {
//        return this.sourceType;
//    }
//    public void setSourceType(String sourceType) {
//        this.sourceType = sourceType;
//    }
//
//    @Column(name = "code", nullable = false)
//    public String getCode() {
//        return this.code;
//    }
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    @Column(name = "name", nullable = false)
//    public String getName() {
//        return this.name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Column(name = "description")
//    public String getDescription() {
//        return this.description;
//    }
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    @Column(name = "create_date")
//    public Date getCreateDate() {
//        return this.create_date;
//    }
//    public void setCreateDate(Date create_date) {
//        this.create_date = create_date;
//    }
//
//    @Column(name = "create_user")
//    public String getCreateUser() {
//        return this.create_user;
//    }
//    public void setCreateUser(String create_user) {
//        this.create_user = create_user;
//    }
//
//    @Column(name = "update_date")
//    public Date getUpdateDate() {
//        return this.update_date;
//    }
//    public void setUpdateDate(Date update_date) {
//        this.update_date = update_date;
//    }
//
//    @Column(name = "update_user")
//    public String getUpdateUser() {
//        return this.update_user;
//    }
//    public void setUpdateUser(String update_user) {
//        this.update_user = update_user;
//    }
//
//    @Transient
//    public int getHashCode() {
//        hashCode = Objects.hash(code, create_date, create_user, description, id, name, update_date, update_user, sourceType);
//        return hashCode;
//    }
//    public void setHashCode(int hashCode){
//        this.hashCode = hashCode;
//    }
//
//    @Transient
//    public String getOperationType() {
//        return this.OperationType;
//    }
//    public void setOperationType(String operationType) {
//        this.OperationType = operationType;
//    }
//
//    @Transient
//    public String getSourceValue() {
//        return sourceValue;
//    }
//
//    public void setSourceValue(String sourceValue) {
//        this.sourceValue = sourceValue;
//    }
//}