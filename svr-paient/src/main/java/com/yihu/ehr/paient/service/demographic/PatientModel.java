package com.yihu.ehr.paient.service.demographic;


import com.yihu.ehr.model.address.MAddress;

/**
 * Created by zqb on 2015/8/14.
 */
public class PatientModel {
    String name="";
    String idCardNo="";
    String gender="";
    String nation="";
    String nativePlace="";
    String martialStatus="";
    String birthday="";
    MAddress birthPlace=null;
    MAddress homeAddress=null;
    MAddress workAddress=null;
    String residenceType="";
    String tel="";
    String email="";
    String birthPlaceFull="";
    String homeAddressFull="";
    String workAddressFull="";
    String password="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getMartialStatus() {
        return martialStatus;
    }

    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public MAddress getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(MAddress birthPlace) {
        this.birthPlace = birthPlace;
    }

    public MAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(MAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public MAddress getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(MAddress workAddress) {
        this.workAddress = workAddress;
    }

    public String getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(String residenceType) {
        this.residenceType = residenceType;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthPlaceFull() {
        return birthPlaceFull;
    }

    public void setBirthPlaceFull(String birthPlaceFull) {
        this.birthPlaceFull = birthPlaceFull;
    }

    public String getHomeAddressFull() {
        return homeAddressFull;
    }

    public void setHomeAddressFull(String homeAddressFull) {
        this.homeAddressFull = homeAddressFull;
    }

    public String getWorkAddressFull() {
        return workAddressFull;
    }

    public void setWorkAddressFull(String workAddressFull) {
        this.workAddressFull = workAddressFull;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
