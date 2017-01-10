package com.yihu.ehr.patient.service.arapply;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@Entity
@Table(name = "archive_apply")
public class ArApply {

    private int id;                       //编号
    private String name;                 //  申请人姓名
    private String idCard;                // 申请人身份证号
    private Date applyDate;               // 申请时间
    private String status;                   // 审核状态
    private Date auditDate;              //审核时间
    private String auditReason;           //不通过原因
    private String auditor;              //审核人
    private String visDate;                //就诊时间
    private String visOrg;                //就诊机构
    private String visDoctor;           //就诊医生
    private String cardNo;                //卡号
    private String diagnosedResult;   //诊断结果
    private String diagnosedProject;  //检查项目
    private String medicines;          //诊断开药
    private String memo;                //备注

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

    @Column(name = "vis_date", nullable = true)
    public String getVisDate() {
        return visDate;
    }

    public void setVisDate(String visDate) {
        this.visDate = visDate;
    }

    @Column(name = "vis_org", nullable = true)
    public String getVisOrg() {
        return visOrg;
    }

    public void setVisOrg(String visOrg) {
        this.visOrg = visOrg;
    }

    @Column(name = "vis_doctor", nullable = true)
    public String getVisDoctor() {
        return visDoctor;
    }

    public void setVisDoctor(String visDoctor) {
        this.visDoctor = visDoctor;
    }

    @Column(name = "card_no", nullable = true)
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Column(name = "diagnosed_result", nullable = true)
    public String getDiagnosedResult() {
        return diagnosedResult;
    }

    public void setDiagnosedResult(String diagnosedResult) {
        this.diagnosedResult = diagnosedResult;
    }

    @Column(name = "diagnosed_project", nullable = true)
    public String getDiagnosedProject() {
        return diagnosedProject;
    }

    public void setDiagnosedProject(String diagnosedProject) {
        this.diagnosedProject = diagnosedProject;
    }

    @Column(name = "medicines", nullable = true)
    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    @Column(name = "memo", nullable = true)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
