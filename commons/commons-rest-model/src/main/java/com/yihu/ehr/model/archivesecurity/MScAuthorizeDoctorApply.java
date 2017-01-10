package com.yihu.ehr.model.archivesecurity;

import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
public class MScAuthorizeDoctorApply {

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

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getApplicantId() {
        return applicantId;
    }
    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApplyReason() {
        return applyReason;
    }
    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public int getAuthorizeType() {
        return authorizeType;
    }
    public void setAuthorizeType(int authorizeType) {
        this.authorizeType = authorizeType;
    }

    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getAuthorizeScope() {
        return authorizeScope;
    }
    public void setAuthorizeScope(int authorizeScope) {
        this.authorizeScope = authorizeScope;
    }

    public String getRejectReason() {
        return rejectReason;
    }
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Date getApplyTime() {
        return applyTime;
    }
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public int getAuthorizeMode() {
        return authorizeMode;
    }
    public void setAuthorizeMode(int authorizeMode) {
        this.authorizeMode = authorizeMode;
    }

    public Date getAuthorizeTime() {
        return authorizeTime;
    }
    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    public int getAuthorizeStatus() {
        return authorizeStatus;
    }
    public void setAuthorizeStatus(int authorizeStatus) {
        this.authorizeStatus = authorizeStatus;
    }
}
