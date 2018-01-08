package com.yihu.ehr.entity.patient;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@Entity
@Table(name = "authentication")
public class Authentication {

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


    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "id_card", nullable = false)
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Column(name = "apply_date", nullable = false)
    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "audit_date", nullable = true)
    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    @Column(name = "audit_reason", nullable = true)
    public String getAuditReason() {
        return auditReason;
    }

    public void setAuditReason(String auditReason) {
        this.auditReason = auditReason;
    }

    @Column(name = "auditor", nullable = true)
    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @Column(name = "telephone", nullable = true)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "medical_card_type", nullable = true)
    public String getMedicalCardType() {
        return medicalCardType;
    }

    public void setMedicalCardType(String medicalCardType) {
        this.medicalCardType = medicalCardType;
    }

    @Column(name = "medical_card_no", nullable = true)
    public String getMedicalCardNo() {
        return medicalCardNo;
    }

    public void setMedicalCardNo(String medicalCardNo) {
        this.medicalCardNo = medicalCardNo;
    }

    @Column(name = "id_card_effective", nullable = true)
    public String getIdCardEffective() {
        return idCardEffective;
    }

    public void setIdCardEffective(String idCardEffective) {
        this.idCardEffective = idCardEffective;
    }
}
