package com.yihu.ehr.entity.emergency;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity - 排班表
 * Created by progr1mmer on 2017/11/8.
 */
@Entity
@Table(name = "eme_schedule")
@Access(value = AccessType.PROPERTY)
public class Schedule extends BaseIdentityEntity {

    /**
     * 状态
     */
    public enum Status{
        /** 启用 */
        on,
        /** 未启用 */
        off
    }
    // 开始时间
    private Date start;
    // 结束时间
    private Date end;
    // 车牌号码
    private String carId;
    // 执勤人员
    private String dutyName;
    // 工号
    private String dutyNum;
    // 执勤手机号码
    private String dutyPhone;
    // 执勤角色
    private String dutyRole;
    // 状态（只能有一份数据为启用状态）
    private Status status;
    // 主副班
    private Boolean main;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start", nullable = false)
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    @Column(name = "duty_num")
    public String getDutyNum() {
        return dutyNum;
    }

    public void setDutyNum(String dutyNum) {
        this.dutyNum = dutyNum;
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

    @Column(name = "main")
    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }
}
