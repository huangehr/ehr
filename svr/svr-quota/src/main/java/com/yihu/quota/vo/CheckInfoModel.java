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
    private String sex;//性别
    private String sexName;//性别
    private String demographicId;//身份证号
    private String carId;     //就诊卡号
    private String checkCode;//检查code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
    private String symptom;    //并发症名称
    private String symptomCode;//并发症code
    private String fastingBloodGlucoseCode;//空腹血糖编码 1：7.8mmol/l 以下 2：7.8-11.1mmol/l  3:11.1mmol/l 以上
    private String fastingBloodGlucoseName;//空腹血糖值
    private String sugarToleranceCode;//葡萄糖耐量Code  1：4.6-6.1mmol/l  2：6.1-7.0mmol/l 3:7.0mmol/l 以上
    private String sugarTolerance;//葡萄糖耐量值
    private String medicineCode;//用药编码
    private String medicineName;//用药名称

    
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

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
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

    public String getSugarTolerance() {
        return sugarTolerance;
    }

    public void setSugarTolerance(String sugarTolerance) {
        this.sugarTolerance = sugarTolerance;
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
}
