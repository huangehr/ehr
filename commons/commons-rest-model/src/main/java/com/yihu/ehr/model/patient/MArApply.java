package com.yihu.ehr.model.patient;

import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
public class MArApply {

    private int id;                       //编号
    private String name;                 // 申请时间
    private String idCard;                //申请人姓名
    private Date applyDate;               //申请人身份证号
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

    public String getVisDate() {
        return visDate;
    }

    public void setVisDate(String visDate) {
        this.visDate = visDate;
    }

    public String getVisOrg() {
        return visOrg;
    }

    public void setVisOrg(String visOrg) {
        this.visOrg = visOrg;
    }

    public String getVisDoctor() {
        return visDoctor;
    }

    public void setVisDoctor(String visDoctor) {
        this.visDoctor = visDoctor;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getDiagnosedResult() {
        return diagnosedResult;
    }

    public void setDiagnosedResult(String diagnosedResult) {
        this.diagnosedResult = diagnosedResult;
    }

    public String getDiagnosedProject() {
        return diagnosedProject;
    }

    public void setDiagnosedProject(String diagnosedProject) {
        this.diagnosedProject = diagnosedProject;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
