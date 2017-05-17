package com.yihu.ehr.model.report;

import java.util.Date;
import java.util.List;

/**
 * Created by zdm on 2017/5/8.
 */
public class MQcDailyReportResultAnalyse {

    private String id;
    private String city;       //市级
    private String cityName; //市级名称
    private String town;        //区级
    private String townName;    //区级名称
    private Date receiveTime;    //采集日期
    private String eventTime;    //事件时间
    private List<MQcDailyReportResultDetail> qcQuotaResultDetailList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public List<MQcDailyReportResultDetail> getQcQuotaResultDetailList() {
        return qcQuotaResultDetailList;
    }

    public void setQcQuotaResultDetailList(List<MQcDailyReportResultDetail> qcQuotaResultDetailList) {
        this.qcQuotaResultDetailList = qcQuotaResultDetailList;
    }
}
