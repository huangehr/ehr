package com.yihu.ehr.entity.emergency;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * Entity - 定位数据表
 * Created by progr1mmer on 2017/11/7.
 */
@Entity
@Table(name = "eme_position")
@Access(value = AccessType.PROPERTY)
public class Position {

    // UUID
    private String id;
    // 经度
    private BigInteger longitude;
    // 纬度
    private BigInteger latitude;
    // 车牌号码
    private String carId;
    // 时间
    private Date date;
    // 出勤ID
    private int attendanceId;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "longitude", nullable = false)
    public BigInteger getLongitude() {
        return longitude;
    }

    public void setLongitude(BigInteger longitude) {
        this.longitude = longitude;
    }

    @Column(name = "latitude", nullable = false)
    public BigInteger getLatitude() {
        return latitude;
    }

    public void setLatitude(BigInteger latitude) {
        this.latitude = latitude;
    }

    @Column(name = "car_id", nullable = false)
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

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
