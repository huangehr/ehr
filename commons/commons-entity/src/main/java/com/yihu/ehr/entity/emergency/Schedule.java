package com.yihu.ehr.entity.emergency;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity - 排班表
 * Created by progr1mmer on 2017/11/8.
 */
@Entity
@Table(name = "eme_schedule")
@Access(value = AccessType.PROPERTY)
public class Schedule {

    /**
     * 状态
     */
    public enum Status{
        /** 启用 */
        on,
        /** 未启用 */
        off,
    }
    // id
    private int id;
    // 日期
    private Date date;
    // 车牌号码
    private String carId;
    // 医生
    private String doctor;
    // 医生手机号码
    private String doctorPhone;
    // 护士
    private String nurse;
    // 护士手机号码
    private String nursePhone;
    // 司机
    private String driver;
    // 司机手机号码
    private String driverPhone;
    // 增援
    private String reinforce;
    // 增援手机号码
    private String reinforcePhone;
    // 创建者（登陆用户）
    private String creator;
    // 创建时间
    private Date createDate;
    // 更新者
    private String updater;
    // 更新时间
    private Date updateDate;
    // 状态（只能有一份数据为启用状态）
    private Status status;

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

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "car_id", nullable = false)
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Column(name = "doctor", nullable = false)
    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    @Column(name = "doctor_phone", nullable = false)
    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    @Column(name = "nurse", nullable = false)
    public String getNurse() {
        return nurse;
    }

    public void setNurse(String nurse) {
        this.nurse = nurse;
    }

    @Column(name = "nurse_phone", nullable = false)
    public String getNursePhone() {
        return nursePhone;
    }

    public void setNursePhone(String nursePhone) {
        this.nursePhone = nursePhone;
    }

    @Column(name = "driver", nullable = false)
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Column(name = "driver_phone", nullable = false)
    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    @Column(name = "reinforce")
    public String getReinforce() {
        return reinforce;
    }

    public void setReinforce(String reinforce) {
        this.reinforce = reinforce;
    }

    @Column(name = "reinforce_phone")
    public String getReinforcePhone() {
        return reinforcePhone;
    }

    public void setReinforcePhone(String reinforcePhone) {
        this.reinforcePhone = reinforcePhone;
    }

    @Column(name = "creator", nullable = false)
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "create_date", nullable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "updater")
    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @Column(name = "update_date")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "status", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
