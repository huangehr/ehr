package com.yihu.ehr.entity.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 档案认领申请列表
 * Created by hzp on 2017/04/05.
 */
@Entity
@Table(name="archive_apply")
public class ArchiveApply  {

    protected Long id; // 非业务主键
    private String userId;
    private String name;//'申请人姓名',
    private String idCard;//申请人身份证号',
    private Date applyDate;//申请时间',
    private String status;//审核状态',
    private Date auditDate;//审核时间',
    private String auditReason;//不通过原因',
    private String auditor;//审核人',
    private Date visDate;//就诊时间',
    private String visOrg;//就诊机构',
    private String visDoctor;//就诊医生',
    private String cardNo;//医疗卡号',
    private String diagnosedResult;//诊断结果',
    private String diagnosedProject;//检查项目',
    private String medicines;//诊断开药',
    private String memo;//备注',

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "id_card")
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Column(name = "apply_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    @Column(name = "audit_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    @Column(name = "audit_reason")
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

    @Column(name = "vis_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getVisDate() {
        return visDate;
    }

    public void setVisDate(Date visDate) {
        this.visDate = visDate;
    }

    @Column(name = "vis_org")
    public String getVisOrg() {
        return visOrg;
    }

    public void setVisOrg(String visOrg) {
        this.visOrg = visOrg;
    }

    @Column(name = "vis_doctor")
    public String getVisDoctor() {
        return visDoctor;
    }

    public void setVisDoctor(String visDoctor) {
        this.visDoctor = visDoctor;
    }

    @Column(name = "card_no")
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Column(name = "diagnosed_result")
    public String getDiagnosedResult() {
        return diagnosedResult;
    }

    public void setDiagnosedResult(String diagnosedResult) {
        this.diagnosedResult = diagnosedResult;
    }

    @Column(name = "diagnosed_project")
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
