package com.yihu.ehr.standard.model;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Objects;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 16:54:17
 */
@MappedSuperclass
public class BaseCDADocument {
    @Transient
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
    private String type;
    private String printOut;
    private String schema;
    private String sourceId;
    private int hashCode;
    private String fileGroup;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "code", unique = false, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", unique = false, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "create_date", unique = false, nullable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "create_user", unique = false, nullable = false)
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(name = "description", unique = false, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "update_date", unique = false, nullable = true)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "update_user", unique = false, nullable = true)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "print_out", unique = false, nullable = true)
    public String getPrintOut() {
        return printOut;
    }

    public void setPrintOut(String printOut) {
        this.printOut = printOut;
    }

    @Column(name = "schema_path", unique = false, nullable = true)
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Column(name = "source_id", unique = false, nullable = false)
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "file_group", unique = false, nullable = true)
    public String getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(String fileGroup) {
        this.fileGroup = fileGroup;
    }

    @Column(name = "type", unique = false, nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "hash", unique = false, nullable = true)
    public int getHashCode() {
        hashCode = Objects.hash(id, code, createDate, createUser, name, printOut, schema, sourceId);
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Transient
    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}