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
    private Date create_date;
    private String create_user;
    private Date update_date;
    private String update_user;
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

    public String getCreateUser() {
        return this.create_user;
    }
    public void setCreateUser(String create_user) {
        this.create_user = create_user;
    }

    public String getUpdateUser() {
        return this.update_user;
    }
    public void setUpdateUser(String update_user) {
        this.update_user = update_user;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getUpdate_user() {
        return update_user;
    }

    public void setUpdate_user(String update_user) {
        this.update_user = update_user;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }
}