package com.yihu.ehr.paient.service.demographic;

/**
 * Created by zqb on 2015/8/16.
 */
public class PatientBrowseModel {
    Integer order=null;
    String name=null;
    String idCardNo=null;
    String gender=null;
    String genderValue=null;
    String tel=null;
    String homeAddress=null;

    public String getGenderValue() {
        return genderValue;
    }

    public void setGenderValue(String genderValue) {
        this.genderValue = genderValue;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name==null?"":name;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo==null?"":idCardNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender==null?"":gender;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel==null?"":tel;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress==null?"":homeAddress;
    }
}
