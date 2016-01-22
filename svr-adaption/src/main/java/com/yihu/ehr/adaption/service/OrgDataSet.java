package com.yihu.ehr.adaption.service;

import com.yihu.ha.data.sql.SQLGeneralDAO;
import com.yihu.ha.user.model.XUser;

import java.util.Date;

/**
 * 机构数据集实现类.
 * @author linaz
 * @version 1.0
 * @created 23-10月-2015 10:19:06
 */
public class OrgDataSet extends SQLGeneralDAO implements XOrgDataSet {

    long id;
    String code;
    String name;
    Date createDate;
    Date updateDate;
    XUser createUser;
    XUser updateUser;
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

    public XUser getCreateUser() {
        return createUser;
    }

    public void setCreateUser(XUser createUser) {
        this.createUser = createUser;
    }

    public XUser getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(XUser updateUser) {
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

    public XOrgDataSet setNewObject(XOrgDataSet from){
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