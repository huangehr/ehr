package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@javax.persistence.Table(name = "mr_medical_records")
public class MrMedicalRecordsEntity {
    private int id;
    private String patientId;
    private Timestamp medicalTime;
    private String doctorId;
    private String medicalDiagnosis;
    private String medicalDiagnosisCode;
    private String medicalSuggest;
    private String patientCondition;
    private String patientHistoryNow;
    private String patientHistoryPast;
    private String patientAllergy;
    private String patientHistoryFamily;
    private String patientPhysical;
    private Integer firstRecordId;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @javax.persistence.Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @javax.persistence.Column(name = "PATIENT_ID")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    @Basic
    @javax.persistence.Column(name = "MEDICAL_TIME")
    public Timestamp getMedicalTime() {
        return medicalTime;
    }

    public void setMedicalTime(Timestamp medicalTime) {
        this.medicalTime = medicalTime;
    }

    @Basic
    @javax.persistence.Column(name = "DOCTOR_ID")
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }


    @Basic
    @javax.persistence.Column(name = "MEDICAL_DIAGNOSIS")
    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
    }

    @Basic
    @javax.persistence.Column(name = "MEDICAL_DIAGNOSIS_CODE")
    public String getMedicalDiagnosisCode() {
        return medicalDiagnosisCode;
    }

    public void setMedicalDiagnosisCode(String medicalDiagnosisCode) {
        this.medicalDiagnosisCode = medicalDiagnosisCode;
    }

    @Basic
    @javax.persistence.Column(name = "MEDICAL_SUGGEST")
    public String getMedicalSuggest() {
        return medicalSuggest;
    }

    public void setMedicalSuggest(String medicalSuggest) {
        this.medicalSuggest = medicalSuggest;
    }

    @Basic
    @javax.persistence.Column(name = "Patient_condition")
    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    @Basic
    @javax.persistence.Column(name = "Patient_history_now")
    public String getPatientHistoryNow() {
        return patientHistoryNow;
    }

    public void setPatientHistoryNow(String patientHistoryNow) {
        this.patientHistoryNow = patientHistoryNow;
    }

    @Basic
    @javax.persistence.Column(name = "Patient_history_past")
    public String getPatientHistoryPast() {
        return patientHistoryPast;
    }

    public void setPatientHistoryPast(String patientHistoryPast) {
        this.patientHistoryPast = patientHistoryPast;
    }

    @javax.persistence.Column(name = "Patient_allergy")
    public String getPatientAllergy() {
        return patientAllergy;
    }

    public void setPatientAllergy(String patientAllergy) {
        this.patientAllergy = patientAllergy;
    }

    @javax.persistence.Column(name = "Patient_history_family")
    public String getPatientHistoryFamily() {
        return patientHistoryFamily;
    }

    public void setPatientHistoryFamily(String patientHistoryFamily) {
        this.patientHistoryFamily = patientHistoryFamily;
    }

    @javax.persistence.Column(name = "Patient_Physical")
    public String getPatientPhysical() {
        return patientPhysical;
    }

    public void setPatientPhysical(String patientPhysical) {
        this.patientPhysical = patientPhysical;
    }

    @javax.persistence.Column(name = "First_Record_ID")
    public Integer getFirstRecordId() {
        return firstRecordId;
    }

    public void setFirstRecordId(Integer firstRecordId) {
        this.firstRecordId = firstRecordId;
    }

}
