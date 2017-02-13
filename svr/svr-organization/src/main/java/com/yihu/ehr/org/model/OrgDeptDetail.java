package com.yihu.ehr.org.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * create by cws 2017-02-07
 */
@Entity
@Table(name = "org_dept_detail")
@Access(value = AccessType.FIELD)
public class OrgDeptDetail {


    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "dept_id",  nullable = true)
    private String deptId;

    @Column(name = "org_id",nullable = true)
    private String orgId;

    @Column(name = "code",nullable = true)
    private String code;

    @Column(name = "name",nullable = true)
    private String name;

    @Column(name = "national _dept_sn",nullable = true)
    private int nationalDeptSn;

    @Column(name = "phone",nullable = true)
    private String phone;

    @Column(name = "display_status",nullable = true)
    private String displayStatus;

    @Column(name = "glory_id",nullable = true)
    private String gloryId;

    @Column(name = "insert_time",nullable = true)
    private String insertTime;

    @Column(name = "introduction",nullable = true)
    private String introduction;

    @Column(name = "sort_no",nullable = true)
    private String sortNo;

    @Column(name = "place",nullable = true)
    private String place;

    @Column(name = "py_code",nullable = true)
    private String pyCode;

    @Column(name = "type_id",nullable = true)
    private String typeId;

    @Column(name = "update_time",nullable = true)
    private String updateTime;


    public OrgDeptDetail() {
        //tags = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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

    public int getNationalDeptSn() {
        return nationalDeptSn;
    }

    public void setNationalDeptSn(int nationalDeptSn) {
        this.nationalDeptSn = nationalDeptSn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getGloryId() {
        return gloryId;
    }

    public void setGloryId(String gloryId) {
        this.gloryId = gloryId;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}