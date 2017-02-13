package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "it_resource_hardware_master")
@Access(value = AccessType.FIELD)
public class ItResourceHardwareMaster {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "apply_user_id", unique = true, nullable = false)
    private String applyUserId;

    @Column(name = "apply_user_name", unique = true, nullable = false)
    private String applyUserName;

    @Column(name = "org_id", unique = true, nullable = false)
    private String orgId;

    @Column(name = "apply_app_id", unique = true, nullable = false)
    private String applyAppId;

    @Column(name = "apply_date", unique = true, nullable = false)
    private String applyDate;

    @Column(name = "resource_type", unique = true, nullable = false)
    private int resourceType;

    @Column(name = "apply_status", unique = true, nullable = false)
    private int applyStatus;

    @Column(name = "description", unique = true, nullable = false)
    private String description;

    @Column(name = "approver", unique = true, nullable = false)
    private String approver;

    @Column(name = "approval_time", unique = true, nullable = false)
    private Date approvalTime;

    public ItResourceHardwareMaster() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getApplyAppId() {
        return applyAppId;
    }

    public void setApplyAppId(String applyAppId) {
        this.applyAppId = applyAppId;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }
}