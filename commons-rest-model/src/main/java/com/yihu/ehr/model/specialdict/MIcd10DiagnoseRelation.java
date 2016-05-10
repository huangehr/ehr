package com.yihu.ehr.model.specialdict;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yww on 2016/5/6.
 */
public class MIcd10DiagnoseRelation implements Serializable {
    private String id;
    private String icd10Id;
    private String name;
    private String description;
    private String createUser;
    private Date createDate;
    private String updateUser;
    private Date updateDate;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIcd10Id() {
        return icd10Id;
    }
    public void setIcd10Id(String icd10Id) {
        this.icd10Id = icd10Id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
