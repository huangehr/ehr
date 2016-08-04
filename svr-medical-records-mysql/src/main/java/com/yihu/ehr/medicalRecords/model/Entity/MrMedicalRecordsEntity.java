package com.yihu.ehr.medicalRecords.model.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by hzp on 2016/8/4.
 */
@Entity
@Table(name = "mr_medical_records")
public class MrMedicalRecordsEntity {
    private int id;
    private String patientId;
    private Timestamp medicalTime;
    private Timestamp createTime;
    private String doctorId;
    private String medicalDiagnosis;
    private String medicalDiagnosisCode;
    private String medicalSuggest;
    private Integer firstRecordId;
    private String patientName;
    private String demographicId;
    private String sex;
    private String birthday;
    private String isMarried;
    private String phone;
    private String orgName;
    private String orgDept;


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "PATIENT_ID")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    @Basic
    @Column(name = "MEDICAL_TIME")
    public Timestamp getMedicalTime() {
        return medicalTime;
    }

    public void setMedicalTime(Timestamp medicalTime) {
        this.medicalTime = medicalTime;
    }

    @Basic
    @Column(name = "DOCTOR_ID")
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }


    @Basic
    @Column(name = "MEDICAL_DIAGNOSIS")
    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
    }

    @Basic
    @Column(name = "MEDICAL_DIAGNOSIS_CODE")
    public String getMedicalDiagnosisCode() {
        return medicalDiagnosisCode;
    }

    public void setMedicalDiagnosisCode(String medicalDiagnosisCode) {
        this.medicalDiagnosisCode = medicalDiagnosisCode;
    }

    @Basic
    @Column(name = "MEDICAL_SUGGEST")
    public String getMedicalSuggest() {
        return medicalSuggest;
    }

    public void setMedicalSuggest(String medicalSuggest) {
        this.medicalSuggest = medicalSuggest;
    }

    @Basic
    @Column(name = "FIRST_RECORD_ID")
    public Integer getFirstRecordId() {
        return firstRecordId;
    }

    public void setFirstRecordId(Integer firstRecordId) {
        this.firstRecordId = firstRecordId;
    }

    @Basic
    @Column(name = "FIRST_RECORD_ID")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "PATIENT_NAME")
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Basic
    @Column(name = "DEMOGRAPHIC_ID")
    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    @Basic
    @Column(name = "SEX")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Basic
    @Column(name = "BIRTHDAY")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Basic
    @Column(name = "IS_MARRIED")
    public String getIsMarried() {
        return isMarried;
    }

    public void setIsMarried(String isMarried) {
        this.isMarried = isMarried;
    }

    @Basic
    @Column(name = "PHONE")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "ORG_NAME")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "ORG_DEPT")
    public String getOrgDept() {
        return orgDept;
    }

    public void setOrgDept(String orgDept) {
        this.orgDept = orgDept;
    }
}
