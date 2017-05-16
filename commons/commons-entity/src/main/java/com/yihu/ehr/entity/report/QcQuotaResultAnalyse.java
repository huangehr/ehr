package com.yihu.ehr.entity.report;

import org.hibernate.annotations.GenericGenerator;
import  com.yihu.ehr.entity.report.QcQuotaResultDetail;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zdm on 2017/5/16.
 */
@Entity
@Table(name = "qc_quota_result", schema = "", catalog = "healtharchive")
public class QcQuotaResultAnalyse {

    private String id;
    private String city;       //市级
    private String cityName; //市级名称
    private String town;        //区级
    private String townName;    //区级名称
    private Date receiveTime;    //采集日期
    private Date eventTime;    //事件时间


    @Id
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    @Column(name = "id", unique = true, nullable = false, length = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Basic
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    @Basic
    @Column(name = "city_name")
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    @Basic
    @Column(name = "town")
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
    @Basic
    @Column(name = "town_name")
    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    @Basic
    @Column(name = "receive_time")
    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }
    @Basic
    @Column(name = "event_time")
    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }


}
