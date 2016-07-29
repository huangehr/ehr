package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/27.
 */
@Entity
@Table(name = "mr_label", schema = "", catalog = "medical_records")
public class MrLabelEntity {
    private int id;
    private String doctorId;
    private String labelType;
    private String label;
    private Integer labelClass;
    private int usageCount;

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
    @Column(name = "LABEL")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Basic
    @Column(name = "LABEL_CLASS")
    public Integer getLabelClass() {
        return labelClass;
    }

    public void setLabelClass(Integer labelClass) {
        this.labelClass = labelClass;
    }

    @Basic
    @Column(name = "Usage_count")
    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrLabelEntity that = (MrLabelEntity) o;

        if (id != that.id) return false;
        if (usageCount != that.usageCount) return false;
        if (doctorId != null ? !doctorId.equals(that.doctorId) : that.doctorId != null) return false;
        if (labelType != null ? !labelType.equals(that.labelType) : that.labelType != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (labelClass != null ? !labelClass.equals(that.labelClass) : that.labelClass != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (doctorId != null ? doctorId.hashCode() : 0);
        result = 31 * result + (labelType != null ? labelType.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (labelClass != null ? labelClass.hashCode() : 0);
        result = 31 * result + usageCount;
        return result;
    }
}
