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
    //接警电话
    private String alarmTel;
    //呼救地点
    private String callAddr;
    //主诉
    private String chiefComplaint;
    //调派医院
    private String dispatchHospital;
    // 患者人数
    private String patientNum;
    // 病人性别
    private String patientGender;
    // 初步诊断
    private String disease;
    // 送达地点
    private String deliverAddr;
    // 调度人员
    private String creator;
    //备注
    private String remark;


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

    @Column(name = "patient_num", nullable = false)
    public String getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(String patientNum) {
        this.patientNum = patientNum;
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

    @Column(name = "alarm_tel")
    public String getAlarmTel() {
        return alarmTel;
    }

    public void setAlarmTel(String alarmTel) {
        this.alarmTel = alarmTel;
    }

    @Column(name = "call_addr", nullable = false)
    public String getCallAddr() {
        return callAddr;
    }

    public void setCallAddr(String callAddr) {
        this.callAddr = callAddr;
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

    @Column(name = "deliver_addr")
    public String getDeliverAddr() {
        return deliverAddr;
    }

    public void setDeliverAddr(String deliverAddr) {
        this.deliverAddr = deliverAddr;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
