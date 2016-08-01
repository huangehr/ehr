package com.yihu.ehr.medicalRecord.model.DTO;

import com.mysql.fabric.xmlrpc.base.Data;
import com.yihu.ehr.medicalRecord.family.MedicalRecordsFamily;

/**
 * Created by hzp on 2016/8/1.
 */
public class MedicalRecord {
    private String rowkey;
    private Data createTime;
    private Data medicalTime;
    private String doctorId;
    private String doctorName;
    private String title;
    private String orgDept;
    private String orgName;
    private String patientId;
    private String patientName;
    private String demographicId;
    private String sex;
    private String birthday;
    private String isMarried;
    private String phone;
    private String medicalDiagnosis;
    private String medicalDiagnosisCode;
    private String medicalSuggest;
    private String patientCondition;
    private String patientHistoryNow;
    private String patientHistoryPast;
    private String patientAllergy;
    private String patientHistoryFamily;
    private String patientPhysical;
    private String dataFrom;
    private String firstRecordId;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public Data getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Data createTime) {
        this.createTime = createTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getOrgDept() {
        return orgDept;
    }

    public void setOrgDept(String orgDept) {
        this.orgDept = orgDept;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getIsMarried() {
        return isMarried;
    }

    public void setIsMarried(String isMarried) {
        this.isMarried = isMarried;
    }

    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
    }

    public String getMedicalDiagnosisCode() {
        return medicalDiagnosisCode;
    }

    public void setMedicalDiagnosisCode(String medicalDiagnosisCode) {
        this.medicalDiagnosisCode = medicalDiagnosisCode;
    }

    public String getMedicalSuggest() {
        return medicalSuggest;
    }

    public void setMedicalSuggest(String medicalSuggest) {
        this.medicalSuggest = medicalSuggest;
    }

    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    public String getPatientHistoryNow() {
        return patientHistoryNow;
    }

    public void setPatientHistoryNow(String patientHistoryNow) {
        this.patientHistoryNow = patientHistoryNow;
    }

    public String getPatientHistoryPast() {
        return patientHistoryPast;
    }

    public void setPatientHistoryPast(String patientHistoryPast) {
        this.patientHistoryPast = patientHistoryPast;
    }

    public String getPatientAllergy() {
        return patientAllergy;
    }

    public void setPatientAllergy(String patientAllergy) {
        this.patientAllergy = patientAllergy;
    }

    public String getPatientHistoryFamily() {
        return patientHistoryFamily;
    }

    public void setPatientHistoryFamily(String patientHistoryFamily) {
        this.patientHistoryFamily = patientHistoryFamily;
    }

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public String getFirstRecordId() {
        return firstRecordId;
    }

    public void setFirstRecordId(String firstRecordId) {
        this.firstRecordId = firstRecordId;
    }

    public Data getMedicalTime() {
        return medicalTime;
    }

    public void setMedicalTime(Data medicalTime) {
        this.medicalTime = medicalTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPatientPhysical() {
        return patientPhysical;
    }

    public void setPatientPhysical(String patientPhysical) {
        this.patientPhysical = patientPhysical;
    }

    /**
     * 获取列名集合
     */
    public String[] getColumns() {
        return new String[]{
                MedicalRecordsFamily.DataColumns.CreateTime,
                MedicalRecordsFamily.DataColumns.MedicalTime,
                MedicalRecordsFamily.DataColumns.DoctorId,
                MedicalRecordsFamily.DataColumns.DoctorName,
                MedicalRecordsFamily.DataColumns.Title,
                MedicalRecordsFamily.DataColumns.OrgDept,
                MedicalRecordsFamily.DataColumns.OrgName,
                MedicalRecordsFamily.DataColumns.PatientId,
                MedicalRecordsFamily.DataColumns.PatientName,
                MedicalRecordsFamily.DataColumns.DemographicId,
                MedicalRecordsFamily.DataColumns.Sex,
                MedicalRecordsFamily.DataColumns.Birthday,
                MedicalRecordsFamily.DataColumns.IsMarried,
                MedicalRecordsFamily.DataColumns.Phone,
                MedicalRecordsFamily.DataColumns.MedicalDiagnosis,
                MedicalRecordsFamily.DataColumns.MedicalDiagnosisCode,
                MedicalRecordsFamily.DataColumns.MedicalSuggest,
                MedicalRecordsFamily.DataColumns.PatientCondition,
                MedicalRecordsFamily.DataColumns.PatientHistoryNow,
                MedicalRecordsFamily.DataColumns.PatientHistoryPast,
                MedicalRecordsFamily.DataColumns.PatientAllergy,
                MedicalRecordsFamily.DataColumns.PatientHistoryFamily,
                MedicalRecordsFamily.DataColumns.PatientPhysical,
                MedicalRecordsFamily.DataColumns.FirstRecordId,
                MedicalRecordsFamily.DataColumns.DataFrom
        };
    }

    /**
     * 获取列值集合
     */
    public Object[] getValues() {
        return new Object[]{
                getCreateTime(),
                getMedicalTime(),
                getDoctorId(),
                getDoctorName(),
                getTitle(),
                getOrgDept(),
                getOrgName(),
                getPatientId(),
                getPatientName(),
                getDemographicId(),
                getSex(),
                getBirthday(),
                getIsMarried(),
                getPhone(),
                getMedicalDiagnosis(),
                getMedicalDiagnosisCode(),
                getMedicalSuggest(),
                getPatientCondition(),
                getPatientHistoryNow(),
                getPatientHistoryPast(),
                getPatientAllergy(),
                getPatientHistoryFamily(),
                getPatientPhysical(),
                getFirstRecordId(),
                getDataFrom()
        };
    }
}
