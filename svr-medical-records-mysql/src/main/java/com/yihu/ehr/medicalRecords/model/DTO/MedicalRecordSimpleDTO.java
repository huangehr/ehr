package com.yihu.ehr.medicalRecords.model.DTO;

import java.util.Date;

/**
 * Created by hzp on 2016/8/8.
 *  简单病历，用于列表搜索展示
 */
public class MedicalRecordSimpleDTO {

    private String  id;
    private Date medicalTime;
    private String medicalType;
    private String orgName;
    private String orgDept;
    private String doctorName;
    private String doctorTitle;
    private String medicalDiagnosis;
    private String medicalInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getMedicalTime() {
        return medicalTime;
    }

    public void setMedicalTime(Date medicalTime) {
        this.medicalTime = medicalTime;
    }

    public String getMedicalType() {
        return medicalType;
    }

    public void setMedicalType(String medicalType) {
        this.medicalType = medicalType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDept() {
        return orgDept;
    }

    public void setOrgDept(String orgDept) {
        this.orgDept = orgDept;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorTitle() {
        return doctorTitle;
    }

    public void setDoctorTitle(String doctorTitle) {
        this.doctorTitle = doctorTitle;
    }

    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
    }

    public String getMedicalInfo() {
        return medicalInfo;
    }

    public void setMedicalInfo(String medicalInfo) {
        this.medicalInfo = medicalInfo;
    }
}
