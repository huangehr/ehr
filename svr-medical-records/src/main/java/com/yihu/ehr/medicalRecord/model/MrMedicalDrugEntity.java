package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/19.
 */
@Entity
@Table(name = "mr_medical_drug", schema = "", catalog = "medical_records")
public class MrMedicalDrugEntity {
    private int id;
    private int recordsId;
    private String drugName;
    private String drugSpecifications;
    private String drugUse;
    private String drugQuantity;
    private String drugDosage;
    private String drugFrequency;

    @Id
    @Basic
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "RECORDS_ID")
    public int getRecordsId() {
        return recordsId;
    }

    public void setRecordsId(int recordsId) {
        this.recordsId = recordsId;
    }

    @Basic
    @Column(name = "DRUG_NAME")
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    @Basic
    @Column(name = "DRUG_SPECIFICATIONS")
    public String getDrugSpecifications() {
        return drugSpecifications;
    }

    public void setDrugSpecifications(String drugSpecifications) {
        this.drugSpecifications = drugSpecifications;
    }

    @Basic
    @Column(name = "DRUG_USE")
    public String getDrugUse() {
        return drugUse;
    }

    public void setDrugUse(String drugUse) {
        this.drugUse = drugUse;
    }

    @Basic
    @Column(name = "DRUG_QUANTITY")
    public String getDrugQuantity() {
        return drugQuantity;
    }

    public void setDrugQuantity(String drugQuantity) {
        this.drugQuantity = drugQuantity;
    }

    @Basic
    @Column(name = "DRUG_DOSAGE")
    public String getDrugDosage() {
        return drugDosage;
    }

    public void setDrugDosage(String drugDosage) {
        this.drugDosage = drugDosage;
    }

    @Basic
    @Column(name = "DRUG_FREQUENCY")
    public String getDrugFrequency() {
        return drugFrequency;
    }

    public void setDrugFrequency(String drugFrequency) {
        this.drugFrequency = drugFrequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrMedicalDrugEntity that = (MrMedicalDrugEntity) o;

        if (id != that.id) return false;
        if (recordsId != that.recordsId) return false;
        if (drugName != null ? !drugName.equals(that.drugName) : that.drugName != null) return false;
        if (drugSpecifications != null ? !drugSpecifications.equals(that.drugSpecifications) : that.drugSpecifications != null)
            return false;
        if (drugUse != null ? !drugUse.equals(that.drugUse) : that.drugUse != null) return false;
        if (drugQuantity != null ? !drugQuantity.equals(that.drugQuantity) : that.drugQuantity != null) return false;
        if (drugDosage != null ? !drugDosage.equals(that.drugDosage) : that.drugDosage != null) return false;
        if (drugFrequency != null ? !drugFrequency.equals(that.drugFrequency) : that.drugFrequency != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + recordsId;
        result = 31 * result + (drugName != null ? drugName.hashCode() : 0);
        result = 31 * result + (drugSpecifications != null ? drugSpecifications.hashCode() : 0);
        result = 31 * result + (drugUse != null ? drugUse.hashCode() : 0);
        result = 31 * result + (drugQuantity != null ? drugQuantity.hashCode() : 0);
        result = 31 * result + (drugDosage != null ? drugDosage.hashCode() : 0);
        result = 31 * result + (drugFrequency != null ? drugFrequency.hashCode() : 0);
        return result;
    }
}
