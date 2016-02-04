package com.yihu.ehr.model.patient;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.dict.MConventionalDict;

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
    private MAddress birthPlace;                    // 出生地
    private MAddress nativePlace;                    // 籍贯
    private String email;                            // 邮箱
    private MConventionalDict gender;                            // 性别
    private String name;                            // 姓名
    private MConventionalDict martialStatus;            // 婚姻情况
    private MConventionalDict nation;                            // 民族
    private MConventionalDict residenceType;            // 户口性质（农村、城镇）
    private MAddress workAddress;                    // 工作地址
    private MAddress homeAddress;                    // 家庭地址
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

    public MAddress getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(MAddress birthPlace) {
        this.birthPlace = birthPlace;
    }

    public MAddress getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(MAddress nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MConventionalDict getGender() {
        return gender;
    }

    public void setGender(MConventionalDict gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MConventionalDict getMartialStatus() {
        return martialStatus;
    }

    public void setMartialStatus(MConventionalDict martialStatus) {
        this.martialStatus = martialStatus;
    }

    public MConventionalDict getNation() {
        return nation;
    }

    public void setNation(MConventionalDict nation) {
        this.nation = nation;
    }

    public MConventionalDict getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(MConventionalDict residenceType) {
        this.residenceType = residenceType;
    }

    public MAddress getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(MAddress workAddress) {
        this.workAddress = workAddress;
    }

    public MAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(MAddress homeAddress) {
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

