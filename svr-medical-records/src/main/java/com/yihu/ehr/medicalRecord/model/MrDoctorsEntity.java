package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_doctors", schema = "", catalog = "medical_records")
public class MrDoctorsEntity {
    private String id;
    private String name;
    private String demographicId;
    private String sex;
    private Timestamp birthday;
    private String orgCode;
    private String orgName;
    private String orgDept;
    private String title;
    private String good;
    private String photo;
    private String phone;
    private String status;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
    @Column(name = "ORG_CODE")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Basic
    @Column(name = "ORG_NAME")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "ORG_DEPT")
    public String getOrgDept() {
        return orgDept;
    }

    public void setOrgDept(String orgDept) {
        this.orgDept = orgDept;
    }

    @Basic
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "GOOD")
    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
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
    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrDoctorsEntity that = (MrDoctorsEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (demographicId != null ? !demographicId.equals(that.demographicId) : that.demographicId != null)
            return false;
        if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null) return false;
        if (orgCode != null ? !orgCode.equals(that.orgCode) : that.orgCode != null) return false;
        if (orgName != null ? !orgName.equals(that.orgName) : that.orgName != null) return false;
        if (orgDept != null ? !orgDept.equals(that.orgDept) : that.orgDept != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (good != null ? !good.equals(that.good) : that.good != null) return false;
        if (photo != null ? !photo.equals(that.photo) : that.photo != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }
}
