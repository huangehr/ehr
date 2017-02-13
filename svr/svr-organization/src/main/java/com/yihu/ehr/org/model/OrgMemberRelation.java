package com.yihu.ehr.org.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * create by cws 2017-02-07
 */
@Entity
@Table(name = "org_member_relation")
@Access(value = AccessType.FIELD)
public class OrgMemberRelation {


    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "org_id",nullable = true)
    private String orgId;

    @Column(name = "org_name",nullable = true)
    private String orgName;

    @Column(name = "parent_dept_id",  nullable = true)
    private String parentDeptId;

    @Column(name = "parent_dept_name",  nullable = true)
    private String parentDeptName;

    @Column(name = "dept_id",  nullable = true)
    private int deptId;

    @Column(name = "dept_name",nullable = true)
    private String deptName;

    @Column(name = "duty_name",nullable = true)
    private String dutyName;

    @Column(name = "national _dept_sn",nullable = true)
    private int nationalDeptSn;

    @Column(name = "user_id",nullable = true)
    private String userId;

    @Column(name = "user_name",nullable = true)
    private String userName;

    @Column(name = "parent_user_id",nullable = true)
    private String parentUserId;

    @Column(name = "parent_user_name",nullable = true)
    private String parentUserName;

    @Column(name = "remark",nullable = true)
    private String remark;

    @Column(name = "status",nullable = true)
    private String status;

    public OrgMemberRelation() {
        //tags = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(String parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    public String getParentDeptName() {
        return parentDeptName;
    }

    public void setParentDeptName(String parentDeptName) {
        this.parentDeptName = parentDeptName;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public int getNationalDeptSn() {
        return nationalDeptSn;
    }

    public void setNationalDeptSn(int nationalDeptSn) {
        this.nationalDeptSn = nationalDeptSn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(String parentUserId) {
        this.parentUserId = parentUserId;
    }

    public String getParentUserName() {
        return parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}