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
    private String totalNum;    //应收数
    private String realNum;    //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
    private String errorNum;    //错误数量（该字段只针对数据元的准确性统计）
    private String timelyNum;    //及时采集的档案数量
    private String an;    //同比：与去年的当天&周&月相比
    private String mom;    //环比：与前一天&周&月环比
    private String scaleType;//总体-1，同比-2，环比-3
    private String arIntegrity;//档案完整性
    private String dsIntegrity;//数据集完整性
    private String mdIntegrity;//数据元完整性
    private String mdAccuracy;//数据元准确性
    private String arTimely;//档案及时性
    private String hpTimely;//住院及时性
    private String opTimely;//门诊及时性
    private String arIntegritySta;//档案完整性达标值
    private String dsIntegritySta;//数据集完整性达标值
    private String mdIntegritySta;//数据元完整性达标值
    private String mdAccuracySta;//数据元准确性达标值
    private String arTimelySta;//档案及时性达标值
    private String hpTimelySta;//住院及时性达标值
    private String opTimelySta;//门诊及时性达标值





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

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getRealNum() {
        return realNum;
    }

    public void setRealNum(String realNum) {
        this.realNum = realNum;
    }

    public String getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(String errorNum) {
        this.errorNum = errorNum;
    }

    public String getTimelyNum() {
        return timelyNum;
    }

    public void setTimelyNum(String timelyNum) {
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

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }

    public String getArIntegrity() {
        return arIntegrity;
    }

    public void setArIntegrity(String arIntegrity) {
        this.arIntegrity = arIntegrity;
    }

    public String getDsIntegrity() {
        return dsIntegrity;
    }

    public void setDsIntegrity(String dsIntegrity) {
        this.dsIntegrity = dsIntegrity;
    }

    public String getMdIntegrity() {
        return mdIntegrity;
    }

    public void setMdIntegrity(String mdIntegrity) {
        this.mdIntegrity = mdIntegrity;
    }

    public String getMdAccuracy() {
        return mdAccuracy;
    }

    public void setMdAccuracy(String mdAccuracy) {
        this.mdAccuracy = mdAccuracy;
    }

    public String getArTimely() {
        return arTimely;
    }

    public void setArTimely(String arTimely) {
        this.arTimely = arTimely;
    }

    public String getHpTimely() {
        return hpTimely;
    }

    public void setHpTimely(String hpTimely) {
        this.hpTimely = hpTimely;
    }

    public String getOpTimely() {
        return opTimely;
    }

    public void setOpTimely(String opTimely) {
        this.opTimely = opTimely;
    }

    public String getArIntegritySta() {
        return arIntegritySta;
    }

    public void setArIntegritySta(String arIntegritySta) {
        this.arIntegritySta = arIntegritySta;
    }

    public String getDsIntegritySta() {
        return dsIntegritySta;
    }

    public void setDsIntegritySta(String dsIntegritySta) {
        this.dsIntegritySta = dsIntegritySta;
    }

    public String getMdIntegritySta() {
        return mdIntegritySta;
    }

    public void setMdIntegritySta(String mdIntegritySta) {
        this.mdIntegritySta = mdIntegritySta;
    }

    public String getMdAccuracySta() {
        return mdAccuracySta;
    }

    public void setMdAccuracySta(String mdAccuracySta) {
        this.mdAccuracySta = mdAccuracySta;
    }

    public String getArTimelySta() {
        return arTimelySta;
    }

    public void setArTimelySta(String arTimelySta) {
        this.arTimelySta = arTimelySta;
    }

    public String getHpTimelySta() {
        return hpTimelySta;
    }

    public void setHpTimelySta(String hpTimelySta) {
        this.hpTimelySta = hpTimelySta;
    }

    public String getOpTimelySta() {
        return opTimelySta;
    }

    public void setOpTimelySta(String opTimelySta) {
        this.opTimelySta = opTimelySta;
    }
}
