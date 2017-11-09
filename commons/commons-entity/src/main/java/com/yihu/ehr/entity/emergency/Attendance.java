package com.yihu.ehr.entity.emergency;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity - 出勤记录
 * Created by progr1mmer on 2017/11/7.
 */
@Entity
@Table(name = "eme_attendance")
@Access(value = AccessType.PROPERTY)
public class Attendance {
    /**
     * 状态
     */
    public enum Status {
        // 记录中
        active,
        // 完成
        complete
    }
    // id
    private int id;
    // 车牌号码
    private String carId;
    // 开始时间
    private Date startTime;
    // 到达时间
    private Date arrivalTime;
    // 结束时间
    private Date endTime;
    // 状态
    private Status status;
    // 病人姓名
    private String patientName;
    // 病人性别
    private String patientGender;
    // 病情
    private String disease;
    // 操作员
    private String creator;
    // 医生
    private String doctor;
    // 护士
    private String nurse;
    // 驾驶员
    private String driver;


    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "car_id", nullable = false)
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Column(name = "start_time", nullable = false)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "arrival_time")
    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Column(name = "end_time")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "status", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "patient_name", nullable = false)
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Column(name = "patient_gender", nullable = false)
    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    @Column(name = "disease", nullable = false)
    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    @Column(name = "creator", nullable = false)
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "doctor", nullable = false)
    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    @Column(name = "nurse", nullable = false)
    public String getNurse() {
        return nurse;
    }

    public void setNurse(String nurse) {
        this.nurse = nurse;
    }

    @Column(name = "driver", nullable = false)
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
