package com.yihu.ehr.medicalRecord.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@javax.persistence.Table(name = "mr_medical_records")
public class MrMedicalRecordsEntity {
    private int id;

    @Id
    @javax.persistence.Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int patientId;

    @Basic
    @javax.persistence.Column(name = "PATIENT_ID")
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    private String medicalType;

    @Basic
    @javax.persistence.Column(name = "MEDICAL_TYPE")
    public String getMedicalType() {
        return medicalType;
    }

    public void setMedicalType(String medicalType) {
        this.medicalType = medicalType;
    }

    private Timestamp medicalTime;

    @Basic
    @javax.persistence.Column(name = "MEDICAL_TIME")
    public Timestamp getMedicalTime() {
        return medicalTime;
    }

    public void setMedicalTime(Timestamp medicalTime) {
        this.medicalTime = medicalTime;
    }

    private Integer doctorId;

    @Basic
    @javax.persistence.Column(name = "DOCTOR_ID")
    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    private String doctorDept;

    @Basic
    @javax.persistence.Column(name = "DOCTOR_DEPT")
    public String getDoctorDept() {
        return doctorDept;
    }

    public void setDoctorDept(String doctorDept) {
        this.doctorDept = doctorDept;
    }

    private String medicalDiagnosis;

    @Basic
    @javax.persistence.Column(name = "MEDICAL_DIAGNOSIS")
    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
    }

    private String medicalDiagnosisCode;

    @Basic
    @javax.persistence.Column(name = "MEDICAL_DIAGNOSIS_CODE")
    public String getMedicalDiagnosisCode() {
        return medicalDiagnosisCode;
    }

    public void setMedicalDiagnosisCode(String medicalDiagnosisCode) {
        this.medicalDiagnosisCode = medicalDiagnosisCode;
    }

    private String medicalSuggest;

    @Basic
    @javax.persistence.Column(name = "MEDICAL_SUGGEST")
    public String getMedicalSuggest() {
        return medicalSuggest;
    }

    public void setMedicalSuggest(String medicalSuggest) {
        this.medicalSuggest = medicalSuggest;
    }

    private String patientCondition;

    @Basic
    @javax.persistence.Column(name = "Patient_condition")
    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    private String patientHistoryNow;

    @Basic
    @javax.persistence.Column(name = "Patient_history_now")
    public String getPatientHistoryNow() {
        return patientHistoryNow;
    }

    public void setPatientHistoryNow(String patientHistoryNow) {
        this.patientHistoryNow = patientHistoryNow;
    }

    private String patientHistoryPast;

    @Basic
    @javax.persistence.Column(name = "Patient_history_past")
    public String getPatientHistoryPast() {
        return patientHistoryPast;
    }

    public void setPatientHistoryPast(String patientHistoryPast) {
        this.patientHistoryPast = patientHistoryPast;
    }

    private String patientAllergy;
    @javax.persistence.Column(name = "Patient_allergy")
    public String getPatientAllergy() {
        return patientAllergy;
    }

    public void setPatientAllergy(String patientAllergy) {
        this.patientAllergy = patientAllergy;
    }

    private String patientHistoryFamily;

    @javax.persistence.Column(name = "Patient_history_family")
    public String getPatientHistoryFamily() {
        return patientHistoryFamily;
    }

    public void setPatientHistoryFamily(String patientHistoryFamily) {
        this.patientHistoryFamily = patientHistoryFamily;
    }

    private String patientPhysical;

    @javax.persistence.Column(name = "Patient_Physical")
    public String getPatientPhysical() {
        return patientPhysical;
    }

    public void setPatientPhysical(String patientPhysical) {
        this.patientPhysical = patientPhysical;
    }

    private Integer firstRecordId;

    @javax.persistence.Column(name = "First_Record_ID")
    public Integer getFirstRecordId() {
        return firstRecordId;
    }

    public void setFirstRecordId(Integer firstRecordId) {
        this.firstRecordId = firstRecordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrMedicalRecordsEntity that = (MrMedicalRecordsEntity) o;

        if (id != that.id) return false;
        if (patientId != that.patientId) return false;
        if (medicalType != null ? !medicalType.equals(that.medicalType) : that.medicalType != null) return false;
        if (medicalTime != null ? !medicalTime.equals(that.medicalTime) : that.medicalTime != null) return false;
        if (doctorId != null ? !doctorId.equals(that.doctorId) : that.doctorId != null) return false;
        if (doctorDept != null ? !doctorDept.equals(that.doctorDept) : that.doctorDept != null) return false;
        if (medicalDiagnosis != null ? !medicalDiagnosis.equals(that.medicalDiagnosis) : that.medicalDiagnosis != null)
            return false;
        if (medicalDiagnosisCode != null ? !medicalDiagnosisCode.equals(that.medicalDiagnosisCode) : that.medicalDiagnosisCode != null)
            return false;
        if (medicalSuggest != null ? !medicalSuggest.equals(that.medicalSuggest) : that.medicalSuggest != null)
            return false;
        if (patientCondition != null ? !patientCondition.equals(that.patientCondition) : that.patientCondition != null)
            return false;
        if (patientHistoryNow != null ? !patientHistoryNow.equals(that.patientHistoryNow) : that.patientHistoryNow != null)
            return false;
        if (patientHistoryPast != null ? !patientHistoryPast.equals(that.patientHistoryPast) : that.patientHistoryPast != null)
            return false;
        if (patientAllergy != null ? !patientAllergy.equals(that.patientAllergy) : that.patientAllergy != null)
            return false;
        if (patientHistoryFamily != null ? !patientHistoryFamily.equals(that.patientHistoryFamily) : that.patientHistoryFamily != null)
            return false;
        if (patientPhysical != null ? !patientPhysical.equals(that.patientPhysical) : that.patientPhysical != null)
            return false;
        if (firstRecordId != null ? !firstRecordId.equals(that.firstRecordId) : that.firstRecordId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + patientId;
        result = 31 * result + (medicalType != null ? medicalType.hashCode() : 0);
        result = 31 * result + (medicalTime != null ? medicalTime.hashCode() : 0);
        result = 31 * result + (doctorId != null ? doctorId.hashCode() : 0);
        result = 31 * result + (doctorDept != null ? doctorDept.hashCode() : 0);
        result = 31 * result + (medicalDiagnosis != null ? medicalDiagnosis.hashCode() : 0);
        result = 31 * result + (medicalDiagnosisCode != null ? medicalDiagnosisCode.hashCode() : 0);
        result = 31 * result + (medicalSuggest != null ? medicalSuggest.hashCode() : 0);
        result = 31 * result + (patientCondition != null ? patientCondition.hashCode() : 0);
        result = 31 * result + (patientHistoryNow != null ? patientHistoryNow.hashCode() : 0);
        result = 31 * result + (patientHistoryPast != null ? patientHistoryPast.hashCode() : 0);
        result = 31 * result + (patientAllergy != null ? patientAllergy.hashCode() : 0);
        result = 31 * result + (patientHistoryFamily != null ? patientHistoryFamily.hashCode() : 0);
        result = 31 * result + (patientPhysical != null ? patientPhysical.hashCode() : 0);
        result = 31 * result + (firstRecordId != null ? firstRecordId.hashCode() : 0);
        return result;
    }
}
