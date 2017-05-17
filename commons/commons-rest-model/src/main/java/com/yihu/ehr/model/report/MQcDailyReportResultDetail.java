package com.yihu.ehr.model.report;

import java.util.Date;

/**
 * Created by zdm on 2017/5/17
 */
public class MQcDailyReportResultDetail {

    private String id;
    private String city;       //市级
    private String town;        //区级
    private String value;        //统计结果
    private Long quotaId;       //指标ID
    private String quotaName;    //指标名称
    private String orgCode;    //机构code
    private String orgName;    //机构名称
    private String eventTime;    //事件时间
    private Integer totalNum;    //应收数
    private Integer realNum;    //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
    private Integer errorNum;    //错误数量（该字段只针对数据元的准确性统计）
    private Integer timelyNum;    //及时采集的档案数量
    private String an;    //同比：与去年的当天&周&月相比
    private String mom;    //环比：与前一天&周&月环比


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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
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

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
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

    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }
}
