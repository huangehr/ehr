package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_patients", schema = "", catalog = "medical_records")
public class MrPatientsEntity {
    private int id;
    private String name;
    private String demographicId;
    private String sex;
    private Timestamp birthday;
    private String maritalStatus;
    private String photo;
    private String phone;
    private String isVerified;
    private String appUid;
    private String appPatientId;

    @Basic
    @Column(name = "APP_UID")
    public String getAppUid() {
        return appUid;
    }

    public void setAppUid(String appUid) {
        this.appUid = appUid;
    }

    @Basic
    @Column(name = "APP_PATIENT_ID")
    public String getAppPatientId() {
        return appPatientId;
    }

    public void setAppPatientId(String appPatientId) {
        this.appPatientId = appPatientId;
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
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "DEMOGRAPHIC_ID")
    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    @Basic
    @Column(name = "SEX")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Basic
    @Column(name = "BIRTHDAY")
    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    @Basic
    @Column(name = "Marital_status")
    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Basic
    @Column(name = "PHOTO")
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Basic
    @Column(name = "PHONE")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "IS_Verified")
    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }
}
