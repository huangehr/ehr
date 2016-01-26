package com.yihu.ehr.adaption.service;

import com.yihu.ehr.model.user.MUser;

import java.util.Date;

/**
 * 机构数据集实现类.
 * @author linaz
 * @version 1.0
 * @created 23-10月-2015 10:19:06
 */
public class OrgDataSet  {

    long id;
    String code;
    String name;
    Date createDate;
    Date updateDate;
    MUser createUser;
    MUser updateUser;
    String description;
    String organization;
    int sequence;

    public long getId() {
        return  id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public MUser getCreateUser() {
        return createUser;
    }

    public void setCreateUser(MUser createUser) {
        this.createUser = createUser;
    }

    public MUser getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(MUser updateUser) {
        this.updateUser = updateUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public OrgDataSet setNewObject(OrgDataSet from){
        code=from.getCode();
        name=from.getName();
        createDate=from.getCreateDate();
        updateDate=from.getUpdateDate();
        createUser=from.getCreateUser();
        updateUser=from.getUpdateUser();
        description=from.getDescription();
        organization=from.getOrganization();
        sequence=from.getSequence();
        return this;
    }
}