package com.yihu.ehr.model.report;

import java.util.Date;

/**
 * Created by zdm on 2018/5/9
 */
public class MQcDevice {

    /**设备名称*/
    private String deviceName;
    /**机构代码*/
    private String orgCode;
    /**机构名称*/
    private String orgName;
    /**设备种类代码 对应数据字典id 181*/
    private String deviceType;
    /**采购数量*/
    private Integer purchaseNum;
    /**产地*/
    private String originPlace;
    /**厂家*/
    private String manufacturerName;
    /**设备型号*/
    private String deviceModel;
    /**采购时间*/
    private Date purchaseTime;
    /** 新旧情况*/
    private String isNew;
    /**设备价格*/
    private double devicePrice;
    /**试用年限*/
    private Integer yearLimit;
    /**状态*/
    private String status;
    /** 是否配置GPS*/
    private String isGps;
    /** ID */
    private Integer id;
    /** 创建日期 */
    private Date createDate;
    /** 创建者 */
    private String creator;
    /** 修改日期 */
    private Date modifyDate;
    /** 修改者 */
    private String modifier;
    /**行政区划*/
    private String administrativeDivision;
    /**机构类型*/
    private String hosTypeId;
    /** 设备*/
    private String deviceTypeName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Integer purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public String getOriginPlace() {
        return originPlace;
    }

    public void setOriginPlace(String originPlace) {
        this.originPlace = originPlace;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public double getDevicePrice() {
        return devicePrice;
    }

    public void setDevicePrice(double devicePrice) {
        this.devicePrice = devicePrice;
    }

    public Integer getYearLimit() {
        return yearLimit;
    }

    public void setYearLimit(Integer yearLimit) {
        this.yearLimit = yearLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsGps() {
        return isGps;
    }

    public void setIsGps(String isGps) {
        this.isGps = isGps;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getAdministrativeDivision() {
        return administrativeDivision;
    }

    public void setAdministrativeDivision(String administrativeDivision) {
        this.administrativeDivision = administrativeDivision;
    }

    public String getHosTypeId() {
        return hosTypeId;
    }

    public void setHosTypeId(String hosTypeId) {
        this.hosTypeId = hosTypeId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }
}
