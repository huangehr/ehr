package com.yihu.ehr.medicalRecord.model.DTO;

import com.yihu.ehr.medicalRecord.family.MedicalDrugFamily;

import javax.persistence.*;

/**
 * Created by hzp on 2016/8/1.
 */
public class MedicalDrug {
    private String rowkey;
    private String recordRowkey;
    private String prescriptionRowkey;
    private String drugName;
    private String drugSpecifications;
    private String drugUse;
    private String drugQuantity;
    private String drugUnit;
    private String drugDosage;
    private String drugFrequency;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getRecordRowkey() {
        return recordRowkey;
    }

    public void setRecordRowkey(String recordRowkey) {
        this.recordRowkey = recordRowkey;
    }

    public String getPrescriptionRowkey() {
        return prescriptionRowkey;
    }

    public void setPrescriptionRowkey(String prescriptionRowkey) {
        this.prescriptionRowkey = prescriptionRowkey;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugSpecifications() {
        return drugSpecifications;
    }

    public void setDrugSpecifications(String drugSpecifications) {
        this.drugSpecifications = drugSpecifications;
    }

    public String getDrugUse() {
        return drugUse;
    }

    public void setDrugUse(String drugUse) {
        this.drugUse = drugUse;
    }

    public String getDrugQuantity() {
        return drugQuantity;
    }

    public void setDrugQuantity(String drugQuantity) {
        this.drugQuantity = drugQuantity;
    }

    public String getDrugUnit() {
        return drugUnit;
    }

    public void setDrugUnit(String drugUnit) {
        this.drugUnit = drugUnit;
    }

    public String getDrugDosage() {
        return drugDosage;
    }

    public void setDrugDosage(String drugDosage) {
        this.drugDosage = drugDosage;
    }

    public String getDrugFrequency() {
        return drugFrequency;
    }

    public void setDrugFrequency(String drugFrequency) {
        this.drugFrequency = drugFrequency;
    }

    /**
     * 获取列名集合
     */
    public String[] getColumns() {
        return new String[]{
                MedicalDrugFamily.DataColumns.RecordRowkey,
                MedicalDrugFamily.DataColumns.PrescriptionRowkey,
                MedicalDrugFamily.DataColumns.DrugName,
                MedicalDrugFamily.DataColumns.DrugSpecifications,
                MedicalDrugFamily.DataColumns.DrugQuantity,
                MedicalDrugFamily.DataColumns.DrugUnit,
                MedicalDrugFamily.DataColumns.DrugUse,
                MedicalDrugFamily.DataColumns.DrugDosage,
                MedicalDrugFamily.DataColumns.DrugFrequency
        };
    }

    /**
     * 获取列值集合
     */
    public Object[] getValues() {
        return new Object[]{
                getRecordRowkey(),
                getPrescriptionRowkey(),
                getDrugName(),
                getDrugSpecifications(),
                getDrugQuantity(),
                getDrugUnit(),
                getDrugUse(),
                getDrugDosage(),
                getDrugFrequency()
        };
    }
}
