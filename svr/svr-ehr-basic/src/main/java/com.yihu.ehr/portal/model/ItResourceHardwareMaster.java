package com.yihu.ehr.portal.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "it_resource_hardware_master", schema = "", catalog = "healtharchive")
public class ItResourceHardwareMaster {
    private int id;
    private String applyUserId;
    private String applyUserName;
    private String orgId;
    private String applyAppId;
    private Timestamp applyDate;
    private Integer resourceType;
    private Integer applyStatus;
    private String description;
    private String approver;
    private Timestamp approvalTime;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "apply_user_id", nullable = true, insertable = true, updatable = true)
    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    @Basic
    @Column(name = "apply_user_name", nullable = true, insertable = true, updatable = true)
    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    @Basic
    @Column(name = "org_id", nullable = true, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Basic
    @Column(name = "apply_app_id", nullable = true, insertable = true, updatable = true)
    public String getApplyAppId() {
        return applyAppId;
    }

    public void setApplyAppId(String applyAppId) {
        this.applyAppId = applyAppId;
    }

    @Basic
    @Column(name = "apply_date", nullable = true, insertable = true, updatable = true)
    public Timestamp getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Timestamp applyDate) {
        this.applyDate = applyDate;
    }

    @Basic
    @Column(name = "resource_type", nullable = true, insertable = true, updatable = true)
    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    @Basic
    @Column(name = "apply_status", nullable = true, insertable = true, updatable = true)
    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "approver", nullable = true, insertable = true, updatable = true)
    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    @Basic
    @Column(name = "approval_time", nullable = true, insertable = true, updatable = true)
    public Timestamp getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Timestamp approvalTime) {
        this.approvalTime = approvalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItResourceHardwareMaster that = (ItResourceHardwareMaster) o;

        if (id != that.id) return false;
        if (applyUserId != null ? !applyUserId.equals(that.applyUserId) : that.applyUserId != null) return false;
        if (applyUserName != null ? !applyUserName.equals(that.applyUserName) : that.applyUserName != null)
            return false;
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) return false;
        if (applyAppId != null ? !applyAppId.equals(that.applyAppId) : that.applyAppId != null) return false;
        if (applyDate != null ? !applyDate.equals(that.applyDate) : that.applyDate != null) return false;
        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;
        if (applyStatus != null ? !applyStatus.equals(that.applyStatus) : that.applyStatus != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (approver != null ? !approver.equals(that.approver) : that.approver != null) return false;
        if (approvalTime != null ? !approvalTime.equals(that.approvalTime) : that.approvalTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (applyUserId != null ? applyUserId.hashCode() : 0);
        result = 31 * result + (applyUserName != null ? applyUserName.hashCode() : 0);
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (applyAppId != null ? applyAppId.hashCode() : 0);
        result = 31 * result + (applyDate != null ? applyDate.hashCode() : 0);
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        result = 31 * result + (applyStatus != null ? applyStatus.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (approver != null ? approver.hashCode() : 0);
        result = 31 * result + (approvalTime != null ? approvalTime.hashCode() : 0);
        return result;
    }
}
