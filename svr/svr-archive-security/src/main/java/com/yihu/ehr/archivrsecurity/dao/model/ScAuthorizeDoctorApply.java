package com.yihu.ehr.archivrsecurity.dao.model;

import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
@Entity
@Table(name = "sc_authorize_doctor_apply")
public class ScAuthorizeDoctorApply {

    private String id;
    private String identityId;
    private String applicantId;
    private String applicantName;
    private String appId;
    private String applyReason;
    private int authorizeType;
    private Date startTime;
    private Date endTime;
    private int authorizeScope;
    private String rejectReason;
    private int authorizeStatus;
    private Date applyTime;
    private int authorizeMode;
    private Date authorizeTime;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "identity_id",nullable = false)
    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    @Column(name = "applicant_id",nullable = false)
    public String getApplicantId() {
        return applicantId;
    }
    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    @Column(name = "applicant_name",nullable = false)
    public String getApplicantName() {
        return applicantName;
    }
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    @Column(name = "app_id",nullable = false)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "apply_reason")
    public String getApplyReason() {
        return applyReason;
    }
    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    @Column(name = "authorize_type",nullable = false)
    public int getAuthorizeType() {
        return authorizeType;
    }
    public void setAuthorizeType(int authorizeType) {
        this.authorizeType = authorizeType;
    }

    @Column(name = "start_time")
    @DateTimeFormat(pattern = DateTimeUtil.simpleDateTimePattern)
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "end_time")
    @DateTimeFormat(pattern = DateTimeUtil.simpleDateTimePattern)
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "authorize_scope",nullable = false)
    public int getAuthorizeScope() {
        return authorizeScope;
    }
    public void setAuthorizeScope(int authorizeScope) {
        this.authorizeScope = authorizeScope;
    }

    @Column(name = "reject_reason")
    public String getRejectReason() {
        return rejectReason;
    }
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    @Column(name = "apply_time")
    @DateTimeFormat(pattern = DateTimeUtil.simpleDateTimePattern)
    public Date getApplyTime() {
        return applyTime;
    }
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    @Column(name = "authorize_mode")
    public int getAuthorizeMode() {
        return authorizeMode;
    }
    public void setAuthorizeMode(int authorizeMode) {
        this.authorizeMode = authorizeMode;
    }

    @Column(name = "authorize_time")
    @DateTimeFormat(pattern = DateTimeUtil.simpleDateTimePattern)
    public Date getAuthorizeTime() {
        return authorizeTime;
    }
    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    @Column(name = "authorize_status")
    public int getAuthorizeStatus() {
        return authorizeStatus;
    }
    public void setAuthorizeStatus(int authorizeStatus) {
        this.authorizeStatus = authorizeStatus;
    }
}
