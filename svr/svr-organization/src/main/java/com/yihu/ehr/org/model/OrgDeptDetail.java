package com.yihu.ehr.org.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "org_dept_detail", schema = "", catalog = "healtharchive")
public class OrgDeptDetail {
    private int id;
    private int deptId;
    private String nationalDeptSn;
    private String code;
    private String name;
    private String phone;
    private Integer displayStatus;
    private String gloryId;
    private String orgId;
    private String introduction;
    private Integer sortNo;
    private String place;
    private String pyCode;
    private Integer typeId;
    private Timestamp insertTime;
    private Timestamp updateTime;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "dept_id", nullable = false, insertable = true, updatable = true)
    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    @Column(name = "national_dept_sn", nullable = true, insertable = true, updatable = true)
    public String getNationalDeptSn() {
        return nationalDeptSn;
    }

    public void setNationalDeptSn(String nationalDeptSn) {
        this.nationalDeptSn = nationalDeptSn;
    }

    @Column(name = "code", nullable = true, insertable = true, updatable = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = true, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "phone", nullable = true, insertable = true, updatable = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "display_status", nullable = true, insertable = true, updatable = true)
    public Integer getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Integer displayStatus) {
        this.displayStatus = displayStatus;
    }

    @Column(name = "glory_id", nullable = true, insertable = true, updatable = true)
    public String getGloryId() {
        return gloryId;
    }

    public void setGloryId(String gloryId) {
        this.gloryId = gloryId;
    }

    @Column(name = "org_id", nullable = true, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "introduction", nullable = true, insertable = true, updatable = true)
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Column(name = "sort_no", nullable = true, insertable = true, updatable = true)
    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    @Column(name = "place", nullable = true, insertable = true, updatable = true)
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Column(name = "py_code", nullable = true, insertable = true, updatable = true)
    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    @Column(name = "type_id", nullable = true, insertable = true, updatable = true)
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Column(name = "insert_time", nullable = true, insertable = true, updatable = true)
    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    @Column(name = "update_time", nullable = true, insertable = true, updatable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgDeptDetail that = (OrgDeptDetail) o;

        if (id != that.id) return false;
        if (deptId != that.deptId) return false;
        if (nationalDeptSn != null ? !nationalDeptSn.equals(that.nationalDeptSn) : that.nationalDeptSn != null)
            return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (displayStatus != null ? !displayStatus.equals(that.displayStatus) : that.displayStatus != null)
            return false;
        if (gloryId != null ? !gloryId.equals(that.gloryId) : that.gloryId != null) return false;
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) return false;
        if (introduction != null ? !introduction.equals(that.introduction) : that.introduction != null) return false;
        if (sortNo != null ? !sortNo.equals(that.sortNo) : that.sortNo != null) return false;
        if (place != null ? !place.equals(that.place) : that.place != null) return false;
        if (pyCode != null ? !pyCode.equals(that.pyCode) : that.pyCode != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (insertTime != null ? !insertTime.equals(that.insertTime) : that.insertTime != null) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + deptId;
        result = 31 * result + (nationalDeptSn != null ? nationalDeptSn.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (displayStatus != null ? displayStatus.hashCode() : 0);
        result = 31 * result + (gloryId != null ? gloryId.hashCode() : 0);
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (introduction != null ? introduction.hashCode() : 0);
        result = 31 * result + (sortNo != null ? sortNo.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (pyCode != null ? pyCode.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (insertTime != null ? insertTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }
}