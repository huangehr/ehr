package com.yihu.ehr.medicalRecord.model;
import javax.persistence.*;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_doctor_medical_records", schema = "", catalog = "medical_records")
public class MrDoctorMedicalRecordsEntity {
    private int id;
    private int doctorId;
    private int recordId;
    private String isCreator;

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "DOCTOR_ID")
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    @Basic
    @Column(name = "RECORD_ID")
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
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

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + doctorId;
        result = 31 * result + recordId;
        result = 31 * result + (isCreator != null ? isCreator.hashCode() : 0);
        return result;
    }
}
