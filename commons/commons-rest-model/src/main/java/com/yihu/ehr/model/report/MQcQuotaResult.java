package com.yihu.ehr.model.report;

import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
public class MQcQuotaResult {

    private String id;
    private Date quotaDate;    //统计时间(yyyy-mm-dd)
    private Long quotaId;       //指标ID
    private String quatoName;    //指标名称
    private String value;        //统计结果
    private String city;       //市级
    private String cityName; //市级名称
    private String town;        //区级
    private String townName;    //区级名称
    private String orgCode;    //机构code
    private String orgName;    //机构名称
    private Date receiveTime;    //采集日期
    private Date eventTime;    //事件时间
    private Integer totalNum;    //应收数
    private Integer realNum;    //实收数
    private Integer errorNum;    //错误数量
    private Integer timelyNum;    //及时采集的档案数量
    private String an;    //同比：与去年的当天&周&月相比
    private String mom;    //环比：与前一天&周&月环比


    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getQuotaDate() {
        return quotaDate;
    }

    public void setQuotaDate(Date quotaDate) {
        this.quotaDate = quotaDate;
    }

    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuatoName() {
        return quatoName;
    }

    public void setQuatoName(String quatoName) {
        this.quatoName = quatoName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getRealNum() {
        return realNum;
    }

    public void setRealNum(Integer realNum) {
        this.realNum = realNum;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    public Integer getTimelyNum() {
        return timelyNum;
    }

    public void setTimelyNum(Integer timelyNum) {
        this.timelyNum = timelyNum;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }
}
