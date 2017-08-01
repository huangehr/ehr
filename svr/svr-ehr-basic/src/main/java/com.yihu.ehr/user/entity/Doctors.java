package com.yihu.ehr.user.entity;
import javax.persistence.*;
import java.util.Date;

/**
 * 2017-02-04 add by hzp
 */
@Entity
@Table(name = "doctors")
@Access(value = AccessType.PROPERTY)
public class Doctors {
    private Long id;
    private String userId;
    private String code;
    private String name;
    private String pyCode;
    private String sex;
    private String photo;
    private String skill;
    private String workPortal;
    private String email;
    private String phone;
    private String secondPhone;
    private String familyTel;
    private String officeTel;
    private String introduction;
    private String jxzc;
    private String lczc;
    private String xlzc;
    private String xzzc;
    private String status;
    private Date insertTime;
    private Date updateTime;
    private String idCardNo;
    private String roleType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "userId",  nullable = false)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "code",  nullable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getWorkPortal() {
        return workPortal;
    }

    public void setWorkPortal(String workPortal) {
        this.workPortal = workPortal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getFamilyTel() {
        return familyTel;
    }

    public void setFamilyTel(String familyTel) {
        this.familyTel = familyTel;
    }

    public String getOfficeTel() {
        return officeTel;
    }

    public void setOfficeTel(String officeTel) {
        this.officeTel = officeTel;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getJxzc() {
        return jxzc;
    }

    public void setJxzc(String jxzc) {
        this.jxzc = jxzc;
    }

    public String getLczc() {
        return lczc;
    }

    public void setLczc(String lczc) {
        this.lczc = lczc;
    }

    public String getXlzc() {
        return xlzc;
    }

    public void setXlzc(String xlzc) {
        this.xlzc = xlzc;
    }

    public String getXzzc() {
        return xzzc;
    }

    public void setXzzc(String xzzc) {
        this.xzzc = xzzc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name = "id_card_no",  nullable = false)
    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Column(name = "role_type")
    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
}