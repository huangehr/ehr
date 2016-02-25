package com.yihu.ehr.agModel.patient;

import com.yihu.ehr.agModel.geogrephy.GeographyModel;

import java.util.Date;

/**
 * Created by AndyCai on 2016/2/25.
 */
public class PatientDetailModel {
    private String idCardNo;                       // 身份证
    private Date birthday;                            // 出生日期
    private GeographyModel birthPlace;                    // 出生地
    private String birthPlaceFull;
    private GeographyModel nativePlace;                    // 籍贯
    private String nativePlaceFull;
    private String email;                            // 邮箱
    private String gender;                            // 性别
    private String name;                            // 姓名
    private String martialStatus;            // 婚姻情况
    private String nation;                            // 民族
    private String residenceType;            // 户口性质（农村、城镇）
    private GeographyModel workAddress;                    // 工作地址
    private String workAddressFull;
    private GeographyModel homeAddress;                    // 家庭地址
    private String homeAddressFull;
    private String password;                        //密码
    private String telphoneNo ;                     // 电话号码，之前是个电话号码列表
    private String picPath = "";
    private String localPath = "";

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

    public GeographyModel getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(GeographyModel birthPlace) {
        this.birthPlace = birthPlace;
    }

    public GeographyModel getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(GeographyModel nativePlace) {
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

    public GeographyModel getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(GeographyModel workAddress) {
        this.workAddress = workAddress;
    }

    public GeographyModel getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(GeographyModel homeAddress) {
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

    public String getBirthPlaceFull() {
        return birthPlaceFull;
    }

    public void setBirthPlaceFull(String birthPlaceFull) {
        this.birthPlaceFull = birthPlaceFull;
    }

    public String getNativePlaceFull() {
        return nativePlaceFull;
    }

    public void setNativePlaceFull(String nativePlaceFull) {
        this.nativePlaceFull = nativePlaceFull;
    }

    public String getWorkAddressFull() {
        return workAddressFull;
    }

    public void setWorkAddressFull(String workAddressFull) {
        this.workAddressFull = workAddressFull;
    }

    public String getHomeAddressFull() {
        return homeAddressFull;
    }

    public void setHomeAddressFull(String homeAddressFull) {
        this.homeAddressFull = homeAddressFull;
    }
}
