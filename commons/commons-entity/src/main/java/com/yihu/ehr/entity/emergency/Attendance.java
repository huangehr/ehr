package com.yihu.ehr.entity.emergency;


import com.fasterxml.jackson.annotation.JsonFormat;
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
        // 开始
        start,
        // 到达
        arrival,
        // 完成
        complete,
        // 意外中止
        discontinue
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
    private Date completeTime;
    // 状态
    private Status status;
    // 接警电话
    private String alarmTel;
    // 呼救地点
    private String callAddress;
    // 主诉
    private String chiefComplaint;
    // 调派医院
    private String dispatchHospital;
    // 患者人数
    private int patientNum;
    // 病人性别
    private String patientGender;
    // 初步诊断
    private String disease;
    // 送达地点
    private String deliverAddress;
    // 调度人员
    private String creator;
    // 备注
    private String remark;
    // 值班id列表
    private String schedules;


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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_time", nullable = false)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "arrival_time")
    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "complete_time")
    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "patient_num", nullable = false)
    public int getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(int patientNum) {
        this.patientNum = patientNum;
    }

    @Column(name = "patient_gender")
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

    @Column(name = "alarm_tel")
    public String getAlarmTel() {
        return alarmTel;
    }

    public void setAlarmTel(String alarmTel) {
        this.alarmTel = alarmTel;
    }

    @Column(name = "call_address", nullable = false)
    public String getCallAddress() {
        return callAddress;
    }

    public void setCallAddress(String callAddress) {
        this.callAddress = callAddress;
    }

    @Column(name = "chief_complaint")
    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    @Column(name = "dispatch_hospital")
    public String getDispatchHospital() {
        return dispatchHospital;
    }

    public void setDispatchHospital(String dispatchHospital) {
        this.dispatchHospital = dispatchHospital;
    }

    @Column(name = "deliver_address")
    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "schedules", nullable = false)
    public String getSchedules() {
        return schedules;
    }

    public void setSchedules(String schedules) {
        this.schedules = schedules;
    }
}
