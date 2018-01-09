package com.yihu.ehr.entity.patient;

import javax.persistence.*;
import java.util.Date;

/**
 * 人口学信息接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 25-5月-2015 19:58:12
 */
@Entity
@Table(name = "demographics")
@Access(value = AccessType.PROPERTY)
public class Demographic {

    private String idCardNo;                       // 身份证
    private Date birthday;                                // 出生日期
    private String birthPlace;                    // 出生地
    private String nativePlace;                    // 籍贯
    private String email;                            // 邮箱
    private String gender;                            // 性别
    private String name;                            // 姓名
    private String martialStatus;                  // 婚姻情况
    private String nation;                            // 民族
    private String residenceType;                  // 户口性质（农村、城镇）
    private String workAddress;                    // 工作地址
    private String homeAddress;                    // 家庭地址
    private String password;                        //密码
    private String telephoneNo ;                     // 电话号码，之前是个电话号码列表
    private String picPath = "";
    private String localPath = "";
    private Date registerTime;                      //注册时间

    public Demographic() {
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public String getIdCardNo() {
        return idCardNo;
    }
    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }


    @Column(name = "birthday", nullable = true)
    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Column(name = "birth_place", nullable = true)
    public String getBirthPlace() {
        return birthPlace;
    }
    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    @Column(name = "native_place", nullable = true)
    public String getNativePlace() {
        return nativePlace;
    }
    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    @Column(name = "email", nullable = true)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "gender",nullable = true)
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }


    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "martial_status", nullable = true)
    public String getMartialStatus() {
        return martialStatus;
    }
    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus;
    }

    @Column(name = "nation",  nullable = true)
    public String getNation() {
        return nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }

    @Column(name = "residence_type", nullable = true)
    public String getResidenceType() {
        return residenceType;
    }
    public void setResidenceType(String residenceType) {
        this.residenceType = residenceType;
    }

    @Column(name = "work_address",  nullable = true)
    public String getWorkAddress() {
        return workAddress;
    }
    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    @Column(name = "home_address",  nullable = true)
    public String getHomeAddress() {
        return homeAddress;
    }
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Column(name = "password", nullable = true)
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "telphone_number",  nullable = true)
    public String getTelephoneNo() {
        return telephoneNo;
    }
    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }


    @Column(name = "Pic_path",  nullable = true)
    public String getPicPath() {
        return picPath;
    }
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    @Column(name = "local_path",  nullable = true)
    public String getLocalPath() {
        return localPath;
    }
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Column(name = "register_time",  nullable = true)
    public Date getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}

