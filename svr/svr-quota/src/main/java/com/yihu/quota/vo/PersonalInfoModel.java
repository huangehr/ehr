package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by jansey on 2018/03/1.
 */
public class PersonalInfoModel {

    //个人信息 > 姓名，性别，出生日期，出生年份，身份证，就诊卡号，区县，常住地址，常住地址经纬度，疾病名称，疾病code
    @JestId
    private String id;
    private String name;//姓名
    private String sex;//性别
    private String sexName;//性别
    private String birthday;//出生年月
    private String birthYear;//出生年份
    private String demographicId;//身份证号
    private String carId;     //就诊卡号
    private String town;    //区代码
    private String townName;//
    private String address;//常住地址
    private String addressLngLat;//常住地址经纬度
    private String disease;//疾病
    private String diseaseName;
    private Date eventDate;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;//创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLngLat() {
        return addressLngLat;
    }

    public void setAddressLngLat(String addressLngLat) {
        this.addressLngLat = addressLngLat;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}
