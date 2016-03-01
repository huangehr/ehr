package com.yihu.ehr.model.patient;

import java.util.Date;

/**
 * 人口学信息接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 25-5月-2015 19:58:12
 */
public class MDemographicInfo {

    private String idCardNo;                       // 身份证
    private Date birthday;                            // 出生日期
    private String birthPlace;                    // 户籍地址
    private String nativePlace;                    // 籍贯
    private String email;                            // 邮箱
    private String gender;                            // 性别
    private String name;                            // 姓名
    private String martialStatus;            // 婚姻情况
    private String nation;                            // 民族
    private String residenceType;            // 户口性质（农村、城镇）
    private String workAddress;                    // 工作地址
    private String homeAddress;                    // 家庭地址
    private String password;                        //密码
    private String telphoneNo ;                     // 电话号码，之前是个电话号码列表
    private String picPath = "";
    private String localPath = "";

    public MDemographicInfo() {
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMartialStatus() {
        return martialStatus;
    }

    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(String residenceType) {
        this.residenceType = residenceType;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelphoneNo() {
        return telphoneNo;
    }

    public void setTelphoneNo(String telphoneNo) {
        this.telphoneNo = telphoneNo;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}

