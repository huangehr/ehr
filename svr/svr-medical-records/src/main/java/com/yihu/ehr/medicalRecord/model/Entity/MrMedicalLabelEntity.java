package com.yihu.ehr.medicalRecord.model.Entity;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_medical_label", schema = "", catalog = "")
public class MrMedicalLabelEntity {
    private int id;
    private String recordsId;
    private String doctorId;
    private String label;

    @Column(name = "DOCTOR_ID")
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

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
    @Column(name = "RECORDS_ID")
    public String getRecordsId() {
        return recordsId;
    }

    public void setRecordsId(String recordsId) {
        this.recordsId = recordsId;
    }

    @Basic
    @Column(name = "LABEL")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrMedicalLabelEntity that = (MrMedicalLabelEntity) o;

        if (id != that.id) return false;
        if (recordsId != that.recordsId) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }
}
