package com.yihu.ehr.agModel.patient;

import com.yihu.ehr.agModel.geogrephy.GeographyModel;

import java.util.Date;

/**
 * Created by AndyCai on 2016/2/25.
 */
public class PatientDetailModel {
    private String idCardNo;                       // 身份证
    private Date birthday;                            // 出生日期
    private String birthPlace;
    private GeographyModel birthPlaceInfo;                    // 户籍地址
    private String birthPlaceFull;
    private String nativePlace;
    private String email;                            // 邮箱
    private String gender;                            // 性别
    private String name;                            // 姓名
    private String martialStatus;            // 婚姻情况
    private String martialStatusName;
    private String nation;                            // 民族
    private String nationName;
    private String residenceType;            // 户口性质（农村、城镇）
    private String residenceTypeName;
    private String workAddress;
    private GeographyModel workAddressInfo;                    // 工作地址
    private String workAddressFull;
    private String homeAddress;
    private GeographyModel homeAddressInfo;                    // 家庭地址
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

    public String getMartialStatusName() {
        return martialStatusName;
    }

    public void setMartialStatusName(String martialStatusName) {
        this.martialStatusName = martialStatusName;
    }

    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    public String getResidenceTypeName() {
        return residenceTypeName;
    }

    public void setResidenceTypeName(String residenceTypeName) {
        this.residenceTypeName = residenceTypeName;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public GeographyModel getBirthPlaceInfo() {
        return birthPlaceInfo;
    }

    public void setBirthPlaceInfo(GeographyModel birthPlaceInfo) {
        this.birthPlaceInfo = birthPlaceInfo;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
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

    public GeographyModel getWorkAddressInfo() {
        return workAddressInfo;
    }

    public void setWorkAddressInfo(GeographyModel workAddressInfo) {
        this.workAddressInfo = workAddressInfo;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public GeographyModel getHomeAddressInfo() {
        return homeAddressInfo;
    }

    public void setHomeAddressInfo(GeographyModel homeAddressInfo) {
        this.homeAddressInfo = homeAddressInfo;
    }
}
