package com.yihu.ehr.specialdict.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yww on 2016/5/6.
 */
@Entity
@Table(name = "icd10_diagnose_relation")
@Access(value = AccessType.PROPERTY)
public class Icd10DiagnoseRelation implements Serializable {
    public Icd10DiagnoseRelation (){}
    private long id;
    private String icd10Id;
    private String name;
    private String description;
    private String createUser;
    private Date createDate;
    private String updateUser;
    private Date updateDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "icd10_id",nullable = false)
    public String getIcd10Id() {
        return icd10Id;
    }
    public void setIcd10Id(String icd10Id) {
        this.icd10Id = icd10Id;
    }

    @Column(name = "name",nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description",nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "create_user",nullable = true)
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(name = "create_date",nullable = true)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "update_user",nullable = true)
    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "update_date",nullable = true)
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
