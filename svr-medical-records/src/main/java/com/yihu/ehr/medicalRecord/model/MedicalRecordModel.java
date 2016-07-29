package com.yihu.ehr.medicalRecord.model;

import com.mysql.fabric.xmlrpc.base.Data;

import javax.persistence.Entity;

/**
 * Created by shine on 2016/7/28.
 */
public class MedicalRecordModel {
    private Data createTime;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String orgDept;
    private String orgName;
    private String demographicId;
    private String sex;
    private String birthday;
    private String isMarried;
    private String medicalDiagnosis;
    private String medicalDiagnosisCode;
    private String medicalSuggest;
    private String patientCondition;
    private String patientHistoryNow;
    private String patientHistoryPast;
    private String patientAllergy;
    private String patientHistoryFamily;
    private String dataFrom;
    private String firstRecordId;

    public Data getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Data createTime) {
        this.createTime = createTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getOrgDept() {
        return orgDept;
    }

    public void setOrgDept(String orgDept) {
        this.orgDept = orgDept;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getIsMarried() {
        return isMarried;
    }

    public void setIsMarried(String isMarried) {
        this.isMarried = isMarried;
    }

    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
    }

    public String getMedicalDiagnosisCode() {
        return medicalDiagnosisCode;
    }

    public void setMedicalDiagnosisCode(String medicalDiagnosisCode) {
        this.medicalDiagnosisCode = medicalDiagnosisCode;
    }

    public String getMedicalSuggest() {
        return medicalSuggest;
    }

    public void setMedicalSuggest(String medicalSuggest) {
        this.medicalSuggest = medicalSuggest;
    }

    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    public String getPatientHistoryNow() {
        return patientHistoryNow;
    }

    public void setPatientHistoryNow(String patientHistoryNow) {
        this.patientHistoryNow = patientHistoryNow;
    }

    public String getPatientHistoryPast() {
        return patientHistoryPast;
    }

    public void setPatientHistoryPast(String patientHistoryPast) {
        this.patientHistoryPast = patientHistoryPast;
    }

    public String getPatientAllergy() {
        return patientAllergy;
    }

    public void setPatientAllergy(String patientAllergy) {
        this.patientAllergy = patientAllergy;
    }

    public String getPatientHistoryFamily() {
        return patientHistoryFamily;
    }

    public void setPatientHistoryFamily(String patientHistoryFamily) {
        this.patientHistoryFamily = patientHistoryFamily;
    }

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public String getFirstRecordId() {
        return firstRecordId;
    }

    public void setFirstRecordId(String firstRecordId) {
        this.firstRecordId = firstRecordId;
    }
}
