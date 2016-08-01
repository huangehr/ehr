package com.yihu.ehr.medicalRecord.model.Entity;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/27.
 */
@Entity
@Table(name = "mr_label_class", schema = "", catalog = "medical_records")
public class MrLabelClassEntity {
    private int id;
    private String doctorId;
    private String labelType;
    private int parentId;

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
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Basic
    @Column(name = "LABEL_TYPE")
    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    @Basic
    @Column(name = "PARENT_ID")
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrLabelClassEntity that = (MrLabelClassEntity) o;

        if (id != that.id) return false;
        if (parentId != that.parentId) return false;
        if (doctorId != null ? !doctorId.equals(that.doctorId) : that.doctorId != null) return false;
        if (labelType != null ? !labelType.equals(that.labelType) : that.labelType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (doctorId != null ? doctorId.hashCode() : 0);
        result = 31 * result + (labelType != null ? labelType.hashCode() : 0);
        result = 31 * result + parentId;
        return result;
    }
}
