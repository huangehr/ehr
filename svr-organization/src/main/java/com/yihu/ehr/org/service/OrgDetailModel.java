package com.yihu.ehr.org.service;


public class OrgDetailModel {

    Integer order=null;
    String orgCode="";
    String oryType ="";
    String orgTypeValue= "";
    String fullName="";
    String location="";
    String settledWay="";
    String settledWayValue="";
    int activityFlag;
    String activityName;
    String admin="";
    String tel = "";

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getOryType() {
        return oryType;
    }

    public void setOryType(String oryType) {
        this.oryType = oryType;
    }

    public String getOrgTypeValue() {
        return orgTypeValue;
    }

    public void setOrgTypeValue(String orgTypeValue) {
        this.orgTypeValue = orgTypeValue;
    }

    public String getSettledWayValue() {
        return settledWayValue;
    }

    public void setSettledWayValue(String settledWayValue) {
        this.settledWayValue = settledWayValue;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSettledWay() {
        return settledWay;
    }

    public void setSettledWay(String settledWay) {
        this.settledWay = settledWay;
    }

    public int getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(int activityFlag) {
        this.activityFlag = activityFlag;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}