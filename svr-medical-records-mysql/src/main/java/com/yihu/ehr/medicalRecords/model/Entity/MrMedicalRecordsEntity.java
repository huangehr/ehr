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
    private String doctorId;
    private String medicalDiagnosis;
    private String medicalDiagnosisCode;
    private String medicalSuggest;
    private Integer firstRecordId;

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

}
