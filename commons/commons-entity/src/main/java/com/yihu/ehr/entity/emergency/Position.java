package com.yihu.ehr.entity.emergency;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity - 定位数据表
 * Created by progr1mmer on 2017/11/7.
 */
@Entity
@Table(name = "eme_position")
@Access(value = AccessType.PROPERTY)
public class Position extends BaseIdentityEntity {

    // 经度
    private double longitude;
    // 纬度
    private double latitude;
    // 车牌号码
    private String carId;
    // 时间
    private Date date;
    // 出勤ID
    private int attendanceId;

    @Column(name = "longitude", nullable = false)
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Column(name = "latitude", nullable = false)
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Column(name = "car_id", nullable = false)
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "attendance_id", nullable = false)
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
}
