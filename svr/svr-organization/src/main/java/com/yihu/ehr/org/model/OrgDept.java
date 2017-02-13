package com.yihu.ehr.org.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * create by cws 2017-02-07
 */
@Entity
@Table(name = "org_dept")
@Access(value = AccessType.FIELD)
public class OrgDept {


    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "parent_dept_id",  nullable = true)
    private String parentDeptId;

    @Column(name = "org_id",nullable = true)
    private String orgId;

    @Column(name = "code",nullable = true)
    private String code;

    @Column(name = "name",nullable = true)
    private String name;

    @Column(name = "del_flag",nullable = true)
    private int delFlag;

    @Column(name = "sort_no",nullable = true)
    private String sortNo;

    public OrgDept() {
        //tags = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(String parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
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

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }
}