package com.yihu.ehr.adaption.orgdictitem.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 机构字典项
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Entity
@Table(name = "org_std_dictentry")
public class OrgDictItem {

    long id;
    String code;
    String name;
    Date createDate;
    Date updateDate;
    int sort;
    String description;
    String createUser;
    String updateUser;
    long orgDict;
    String organization;
    int sequence;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Column(name = "description", unique = false, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "create_date", unique = false, nullable = true)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "update_date", unique = false, nullable = true)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "org_dict", unique = false, nullable = true)
    public long getOrgDict() {
        return orgDict;
    }

    public void setOrgDict(long orgDict) {
        this.orgDict = orgDict;
    }

    @Column(name = "sort", unique = false, nullable = false)
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Column(name = "organization", unique = false, nullable = false)
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Column(name = "sequence", unique = false, nullable = false)
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Column(name = "update_user", unique = false, nullable = true)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "create_user", unique = false, nullable = true)
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}