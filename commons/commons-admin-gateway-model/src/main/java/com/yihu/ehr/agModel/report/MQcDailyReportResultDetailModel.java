package com.yihu.ehr.agModel.report;

/**
 * Created by zdm on 2017/5/23.
 */
public class MQcDailyReportResultDetailModel {
    private String eventTime;    //事件时间
    private String orgName;    //机构名称
    private String scaleType;//总体-1，同比-2，环比-3:比例名称
    private String arIntegrity;//档案完整性
    private String dsIntegrity;//数据集完整性
    private String mdIntegrity;//数据元完整性
    private String mdAccuracy;//数据元准确性
    private String arTimely;//档案及时性
    private String hpTimely;//住院及时性
    private String opTimely;//门诊及时性

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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
}
