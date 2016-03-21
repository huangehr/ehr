package com.yihu.ehr.standard.stdsrc.service;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.ObjectId;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.23
 */
@Entity
@Table(name = "std_standard_source")
@Access(value = AccessType.PROPERTY)
public class StandardSource {
    @Value("${admin-region}")
    short adminRegion;

    private String id;
    private String code;
    private Date createDate;
    private String createUser;
    private String description;
    private String name;
    private Date updateDate;
    private String updateUser;
    private String sourceType;

    private String sourceValue;
    private int hashCode;
    private String OperationType;

    public StandardSource() {
        this.createDate = new Date();
        this.createUser = "Sys";
        this.OperationType = "";

        Object objectID = new ObjectId(adminRegion, BizObject.StdProfile);
        this.id = objectID.toString();
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        if(id==null || "".equals(id.trim())){
            Object objectID = new ObjectId(adminRegion, BizObject.StdProfile);
            this.id = objectID.toString();
        }else
            this.id = id;
    }

    @Column(name = "source_type")
    public String getSourceType() {
        return this.sourceType;
    }
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Column(name = "code", nullable = false)
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "create_user")
    public String getCreateUser() {
        return this.createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(name = "update_date")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "update_user")
    public String getUpdateUser() {
        return this.updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "hash")
    public int getHashCode() {
        hashCode = Objects.hash(code, createDate, createUser, description, id, name, updateDate, updateUser, sourceType);
        return hashCode;
    }
    public void setHashCode(int hashCode){
        this.hashCode = hashCode;
    }

    @Transient
    public String getOperationType() {
        return this.OperationType;
    }
    public void setOperationType(String operationType) {
        this.OperationType = operationType;
    }

    @Transient
    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }
}