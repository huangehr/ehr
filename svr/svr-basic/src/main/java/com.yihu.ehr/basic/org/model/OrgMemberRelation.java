package com.yihu.ehr.basic.org.model;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "org_member_relation", schema = "", catalog = "healtharchive")
public class OrgMemberRelation {
    private int id;
    private String orgId;
    private String orgName;
    private Integer parentDeptId;
    private String parentDeptName;
    private Integer deptId;
    private String deptName;
    private String dutyName;
    private String userId;
    private String userName;
    private String parentUserId;
    private String parentUserName;
    private String remark;
    private Integer status;
    private String jkzlDoctorUid; // 总部的医生uid
    private String jkzlUserId; // 总部的医生userid
    private String jkzlDoctorSn; // 总部的坐诊sn
    private String jkzlHosDeptId; // 总部的科室Id

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "org_id", nullable = true, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "org_name", nullable = true)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "parent_dept_id", nullable = true, insertable = true, updatable = true)
    public Integer getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(Integer parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    @Column(name = "parent_dept_name", nullable = true)
    public String getParentDeptName() {
        return parentDeptName;
    }

    public void setParentDeptName(String parentDeptName) {
        this.parentDeptName = parentDeptName;
    }

    @Column(name = "dept_id", nullable = true, insertable = true, updatable = true)
    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    @Column(name = "dept_name", nullable = true)
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Column(name = "duty_name", nullable = true)
    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    @Column(name = "user_id", nullable = true, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "user_name", nullable = true)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "parent_user_id", nullable = true, insertable = true, updatable = true)
    public String getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(String parentUserId) {
        this.parentUserId = parentUserId;
    }

    @Column(name = "parent_user_name", nullable = true)
    public String getParentUserName() {
        return parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    @Column(name = "remark", nullable = true)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "status", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "jkzl_doctor_uid")
    public String getJkzlDoctorUid() {
        return jkzlDoctorUid;
    }

    public void setJkzlDoctorUid(String jkzlDoctorUid) {
        this.jkzlDoctorUid = jkzlDoctorUid;
    }

    @Column(name = "jkzl_user_id")
    public String getJkzlUserId() {
        return jkzlUserId;
    }

    public void setJkzlUserId(String jkzlUserId) {
        this.jkzlUserId = jkzlUserId;
    }

    @Column(name = "jkzl_doctor_sn")
    public String getJkzlDoctorSn() {
        return jkzlDoctorSn;
    }

    public void setJkzlDoctorSn(String jkzlDoctorSn) {
        this.jkzlDoctorSn = jkzlDoctorSn;
    }

    @Column(name = "jkzl_hos_dept_id")
    public String getJkzlHosDeptId() {
        return jkzlHosDeptId;
    }

    public void setJkzlHosDeptId(String jkzlHosDeptId) {
        this.jkzlHosDeptId = jkzlHosDeptId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgMemberRelation that = (OrgMemberRelation) o;

        if (id != that.id) return false;
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) return false;
        if (orgName != null ? !orgName.equals(that.orgName) : that.orgName != null) return false;
        if (parentDeptId != null ? !parentDeptId.equals(that.parentDeptId) : that.parentDeptId != null) return false;
        if (parentDeptName != null ? !parentDeptName.equals(that.parentDeptName) : that.parentDeptName != null)
            return false;
        if (deptId != null ? !deptId.equals(that.deptId) : that.deptId != null) return false;
        if (deptName != null ? !deptName.equals(that.deptName) : that.deptName != null) return false;
        if (dutyName != null ? !dutyName.equals(that.dutyName) : that.dutyName != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (parentUserId != null ? !parentUserId.equals(that.parentUserId) : that.parentUserId != null) return false;
        if (parentUserName != null ? !parentUserName.equals(that.parentUserName) : that.parentUserName != null)
            return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (jkzlDoctorUid != null ? !jkzlDoctorUid.equals(that.jkzlDoctorUid) : that.jkzlDoctorUid != null) return false;
        if (jkzlUserId != null ? !jkzlUserId.equals(that.jkzlUserId) : that.jkzlUserId != null) return false;
        if (jkzlDoctorSn != null ? !jkzlDoctorSn.equals(that.jkzlDoctorSn) : that.jkzlDoctorSn != null) return false;
        if (jkzlHosDeptId != null ? !jkzlHosDeptId.equals(that.jkzlHosDeptId) : that.jkzlHosDeptId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (parentDeptId != null ? parentDeptId.hashCode() : 0);
        result = 31 * result + (parentDeptName != null ? parentDeptName.hashCode() : 0);
        result = 31 * result + (deptId != null ? deptId.hashCode() : 0);
        result = 31 * result + (deptName != null ? deptName.hashCode() : 0);
        result = 31 * result + (dutyName != null ? dutyName.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (parentUserId != null ? parentUserId.hashCode() : 0);
        result = 31 * result + (parentUserName != null ? parentUserName.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (jkzlDoctorUid != null ? jkzlDoctorUid.hashCode() : 0);
        result = 31 * result + (jkzlUserId != null ? jkzlUserId.hashCode() : 0);
        result = 31 * result + (jkzlDoctorSn != null ? jkzlDoctorSn.hashCode() : 0);
        result = 31 * result + (jkzlHosDeptId != null ? jkzlHosDeptId.hashCode() : 0);
        return result;
    }
}