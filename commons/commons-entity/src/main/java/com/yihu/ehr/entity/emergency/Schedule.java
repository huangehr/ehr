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
        off
    }
    // id
    private int id;
    // 开始时间
    private Date start;
    // 结束时间
    private Date end;
    // 车牌号码
    private String carId;
    // 执勤人员
    private String dutyName;
    // 执勤手机号码
    private String dutyPhone;
    // 执勤角色
    private String dutyRole;
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

    @Column(name = "start", nullable = false)
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @Column(name = "end", nullable = false)
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Column(name = "car_id", nullable = false)
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @Column(name = "duty_name", nullable = false)
    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    @Column(name = "duty_phone", nullable = false)
    public String getDutyPhone() {
        return dutyPhone;
    }

    public void setDutyPhone(String dutyPhone) {
        this.dutyPhone = dutyPhone;
    }

    @Column(name = "duty_role", nullable = false)
    public String getDutyRole() {
        return dutyRole;
    }

    public void setDutyRole(String dutyRole) {
        this.dutyRole = dutyRole;
    }

    @Column(name = "status", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
