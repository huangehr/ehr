package com.yihu.ehr.model.org;

import java.sql.Timestamp;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/24.
 */
public class MOrgDeptDetail {
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

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getNationalDeptSn() {
        return nationalDeptSn;
    }

    public void setNationalDeptSn(String nationalDeptSn) {
        this.nationalDeptSn = nationalDeptSn;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Integer displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getGloryId() {
        return gloryId;
    }

    public void setGloryId(String gloryId) {
        this.gloryId = gloryId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
