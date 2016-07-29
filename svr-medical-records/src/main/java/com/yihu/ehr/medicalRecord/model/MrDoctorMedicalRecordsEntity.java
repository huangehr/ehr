package com.yihu.ehr.medicalRecord.model;
import javax.persistence.*;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_doctor_medical_records", schema = "", catalog = "medical_records")
public class MrDoctorMedicalRecordsEntity {
    private int id;
    private String doctorId;
    private String patientId;
    private String recordId;
    private String isCreator;

    @Column(name = "PATIENT_ID")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "RECORD_TYPE")
    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    private String recordType;

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
    @Column(name = "DOCTOR_ID")
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Basic
    @Column(name = "RECORD_ID")
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Basic
    @Column(name = "IS_creator")
    public String getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(String isCreator) {
        this.isCreator = isCreator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrDoctorMedicalRecordsEntity that = (MrDoctorMedicalRecordsEntity) o;

        if (id != that.id) return false;
        if (doctorId != that.doctorId) return false;
        if (recordId != that.recordId) return false;
        if (isCreator != null ? !isCreator.equals(that.isCreator) : that.isCreator != null) return false;

        return true;
    }
}
