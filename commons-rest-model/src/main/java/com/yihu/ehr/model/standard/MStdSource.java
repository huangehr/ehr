package com.yihu.ehr.model.standard;

import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.23
 */
public class MStdSource {
    private String id;
    private String code;
    private String name;
    private Date createDate;
    private String createUser;
    private Date updateDate;
    private String updateUser;
    private String description;

    private String sourceType;
    private String sourceValue;

    public MStdSource() {

    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSourceType() {
        return this.sourceType;
    }
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}