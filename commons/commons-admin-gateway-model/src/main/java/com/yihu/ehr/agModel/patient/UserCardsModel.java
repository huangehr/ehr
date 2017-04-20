package com.yihu.ehr.agModel.patient;

import java.util.Date;

/**
 * Created by AndyCai on 2016/2/22.
 */
public class UserCardsModel {

    private Long id;
    private String userId;//'关联用户id',
    private String cardType;//'卡类别【字典】',
    private String cardTypeName;
    private String cardNo;//'就诊卡号',
    private String local;//'归属地',
    private String ownerName;//'	持卡人姓名',
    private String ownerIdcard;// '	持卡人身份证',
    private String ownerPhone;//持卡人绑定手机',
    private String releaseOrg;//发卡机构',
    private String releaseDate;//发卡时间',
    private String validityDateBegin;//'有效期起始时间',
    private String validityDateEnd;//有效期截止时间',
    private String description;//描述',
    private String openid;//第三方授权码',
    private String status;//卡状态 0无效 1有效',
    private String createDate;//'创建时间',
    private String creater;//创建者',
    private String updateDate;//修改时间',
    private String updater;//修改者',
    private String auditDate;//审核时间
    private String auditor;//审核者
    private String auditStatus;//审核状态 0未审核  1 通过 2 拒绝,
    private String auditReason;//审核不通过原因

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIdcard() {
        return ownerIdcard;
    }

    public void setOwnerIdcard(String ownerIdcard) {
        this.ownerIdcard = ownerIdcard;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getValidityDateBegin() {
        return validityDateBegin;
    }

    public void setValidityDateBegin(String validityDateBegin) {
        this.validityDateBegin = validityDateBegin;
    }

    public String getValidityDateEnd() {
        return validityDateEnd;
    }

    public void setValidityDateEnd(String validityDateEnd) {
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditReason() {
        return auditReason;
    }

    public void setAuditReason(String auditReason) {
        this.auditReason = auditReason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
