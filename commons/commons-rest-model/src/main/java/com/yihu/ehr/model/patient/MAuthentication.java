package com.yihu.ehr.model.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
public class MAuthentication {

    private int id;                       //编号
    private String name;                 //  申请人姓名
    private String idCard;                // 申请人身份证号
    private Date applyDate;               // 申请时间

    private String status;                   // 审核状态
    private Date auditDate;              //审核时间
    private String auditReason;           //不通过原因
    private String auditor;              //审核人

    private String idCardEffective;         // 申请人身份证有效日期
    private String telephone;                //电话
    private String medicalCardType;       //医疗卡类型
    private String medicalCardNo;          //医疗卡卡号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditReason() {
        return auditReason;
    }

    public void setAuditReason(String auditReason) {
        this.auditReason = auditReason;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMedicalCardType() {
        return medicalCardType;
    }

    public void setMedicalCardType(String medicalCardType) {
        this.medicalCardType = medicalCardType;
    }

    public String getMedicalCardNo() {
        return medicalCardNo;
    }

    public void setMedicalCardNo(String medicalCardNo) {
        this.medicalCardNo = medicalCardNo;
    }

    public String getIdCardEffective() {
        return idCardEffective;
    }

    public void setIdCardEffective(String idCardEffective) {
        this.idCardEffective = idCardEffective;
    }
}
