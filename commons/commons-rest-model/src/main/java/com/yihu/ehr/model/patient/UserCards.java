package com.yihu.ehr.model.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.model.common.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户关联卡
 * Created by hzp on 2017/04/05.
 */
@Entity
@Table(name="user_cards")
public class UserCards extends IdEntity {

    private String userId;//'关联用户id',
    private String cardType;//'卡类别【字典】',
    private String cardNo;//'就诊卡号',
    private String local;//'归属地',
    private String ownerName;//'	持卡人姓名',
    private String ownerIdcard;// '	持卡人身份证',
    private String ownerPhone;//持卡人绑定手机',
    private String releaseOrg;//发卡机构',
    private Date releaseDate;//发卡时间',
    private Date validityDateBegin;//'有效期起始时间',
    private Date validityDateEnd;//有效期截止时间',
    private String description;//描述',
    private String openid;//第三方授权码',
    private String status;//卡状态 0无效 1有效',
    private Date createDate;//'创建时间',
    private String creater;//创建者',
    private Date updateDate;//修改时间',
    private String updater;//修改者',
    private Date auditDate;//审核时间
    private String auditor;//审核者
    private String auditStatus;//审核状态 0未审核  1 通过 2 拒绝
    private String auditReason;//审核不通过原因

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "card_type")
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Column(name = "card_no")
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    @Column(name = "owner_name")
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Column(name = "owner_idcard")
    public String getOwnerIdcard() {
        return ownerIdcard;
    }

    public void setOwnerIdcard(String ownerIdcard) {
        this.ownerIdcard = ownerIdcard;
    }

    @Column(name = "owner_phone")
    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    @Column(name = "release_org")
    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg;
    }

    @Column(name = "release_date")
    public Date getReleaseDate() {
        return releaseDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Column(name = "validity_date_begin")
    public Date getValidityDateBegin() {
        return validityDateBegin;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setValidityDateBegin(Date validityDateBegin) {
        this.validityDateBegin = validityDateBegin;
    }

    @Column(name = "validity_date_end")
    public Date getValidityDateEnd() {
        return validityDateEnd;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setValidityDateEnd(Date validityDateEnd) {
        this.validityDateEnd = validityDateEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    @Column(name = "update_date")
    public Date getUpdateDate() {
        return updateDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @Column(name = "audit_date")
    public Date getAuditDate() {
        return auditDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @Column(name = "audit_status")
    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    @Column(name = "audit_reason")
    public String getAuditReason() {
        return auditReason;
    }

    public void setAuditReason(String auditReason) {
        this.auditReason = auditReason;
    }
}
