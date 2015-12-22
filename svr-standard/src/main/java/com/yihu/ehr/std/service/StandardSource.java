package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.BizObject;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Value;


import java.util.Date;
import java.util.Objects;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 16:16:49
 */
public class StandardSource {
    @Value("${admin-region}")
    short adminRegion;

    private String code;
    private Date create_date;
    private String create_user;
    private String description;
    private String id;
    private String name;
    private Date update_date;
    private String update_user;
    private String source_type;
    private String source_type_name;

    private int hashCode;

    public StandardSource() {
        this.create_date = new Date();
        this.create_user = "Sys";
        this.OperationType = "";

        Object objectID = new ObjectId(adminRegion, BizObject.StdArchive);
        id = objectID.toString();
    }

    public void finalize() throws Throwable {
    }

    public String getSourceTypeName() {
        return this.source_type_name;
    }

    /**
     * @param source_type_name
     */
    public void setSourceTypeName(String source_type_name) {
        this.source_type_name = source_type_name;
    }

    public String getSourceType() {
        return this.source_type;
    }

    /**
     * @param source_type
     */
    public void setSourceType(String source_type) {
        this.source_type = source_type;
    }

    public String getCode() {
        return this.code;
    }

    public Date getCreateDate() {
        return this.create_date;
    }

    public String getCreateUser() {
        return this.create_user;
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
        return this.update_date;
    }

    public String getUpdateUser() {
        return this.update_user;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param create_date
     */
    public void setCreateDate(Date create_date) {
        this.create_date = create_date;
    }

    /**
     * @param create_user
     */
    public void setCreateUser(String create_user) {
        this.create_user = create_user;
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
     * @param update_date
     */
    public void setUpdateDate(Date update_date) {
        this.update_date = update_date;
    }

    /**
     * @param update_user
     */
    public void setUpdateUser(String update_user) {
        this.update_user = update_user;
    }

    public int getHashCode() {
        hashCode = Objects.hash(code, create_date, create_user, description, id, name, update_date, update_user, source_type);
        return hashCode;
    }

    String OperationType;

    public String getOperationType() {
        return this.OperationType;
    }

    public void setOperationType(String operationType) {
        this.OperationType = operationType;
    }
}