package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by jansey on 2018/03/1.
 */
public class CheckInfoModel {

    //检查信息 姓名,身份证，就诊卡号,并发症，空腹血糖值，葡萄糖耐量值，用药名称，检查信息code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
    @JestId
    private String id;
    private String name;//姓名
    private int sex;//性别
    private String sexName;//性别
    private String demographicId;//身份证号
    private String cardId;     //就诊卡号
    private String checkCode;//检查code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
    private String symptomCode;//并发症code
    private String symptomName;    //并发症名称
    private String fastingBloodGlucoseCode;//空腹血糖编码 1：7.8mmol/l 以下 2：7.8-11.1mmol/l  3:11.1mmol/l 以上
    private String fastingBloodGlucoseName;//空腹血糖值
    private String sugarToleranceCode;//葡萄糖耐量Code  1：4.6-6.1mmol/l  2：6.1-7.0mmol/l 3:7.0mmol/l 以上
    private String sugarToleranceName;//葡萄糖耐量值
    private String medicineCode;//用药编码
    private String medicineName;//用药名称

    private String diseaseType;//疾病类型 1 I型糖尿病  2 II型糖尿病 3 妊娠糖尿病 4 其他糖尿病
    private String diseaseTypeName;
    private String birthday;//出生年月
    private int birthYear;//出生年份
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
    private Date eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public String getSymptomCode() {
        return symptomCode;
    }

    public void setSymptomCode(String symptomCode) {
        this.symptomCode = symptomCode;
    }

    public String getFastingBloodGlucoseCode() {
        return fastingBloodGlucoseCode;
    }

    public void setFastingBloodGlucoseCode(String fastingBloodGlucoseCode) {
        this.fastingBloodGlucoseCode = fastingBloodGlucoseCode;
    }

    public String getFastingBloodGlucoseName() {
        return fastingBloodGlucoseName;
    }

    public void setFastingBloodGlucoseName(String fastingBloodGlucoseName) {
        this.fastingBloodGlucoseName = fastingBloodGlucoseName;
    }

    public String getSugarToleranceCode() {
        return sugarToleranceCode;
    }

    public void setSugarToleranceCode(String sugarToleranceCode) {
        this.sugarToleranceCode = sugarToleranceCode;
    }

    public String getSugarToleranceName() {
        return sugarToleranceName;
    }

    public void setSugarToleranceName(String sugarToleranceName) {
        this.sugarToleranceName = sugarToleranceName;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public String getDiseaseTypeName() {
        return diseaseTypeName;
    }

    public void setDiseaseTypeName(String diseaseTypeName) {
        this.diseaseTypeName = diseaseTypeName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}
