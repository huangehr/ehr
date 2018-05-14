package com.yihu.ehr.entity.device;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: zhengwei
 * @Date: 2018/5/4 15:47
 * @Description: 设备管理
 */
@Entity
@Table(name = "device")
@Access(value = AccessType.PROPERTY)
public class Device extends BaseIdentityEntity{

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 设备种类代码 对应数据字典id 181
     */
    private String deviceType;

    /**
     * 采购数量
     */
    private Integer purchaseNum;

    /**
     * 产地
     * 1进口 2国产/合资
     */
    private String originPlace;

    /**
     * 厂家
     */
    private String manufacturerName;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 采购时间
     */
    private Date purchaseTime;

    /**
     * 新旧情况 1新设备 2	二手设备
     */
    private String isNew;

    /**
     * 设备价格
     */
    private double devicePrice;

    /**
     * 使用年限
     */
    private Integer yearLimit;

    /**
     * 状态 1启用 2未启用 3报废

     */
    private String status;

    /**
     * 是否配置GPS  1是 0否
     *
     */
    private String isGps;


    @Column(name = "device_name", unique = false, nullable = false, insertable = true, updatable = true, length = 50)
    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String value) {
        this.deviceName = value;
    }

    @Column(name = "org_code", unique = false, nullable = false, insertable = true, updatable = true, length = 20)
    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String value) {
        this.orgCode = value;
    }

    @Column(name = "org_name", unique = false, nullable = false, insertable = true, updatable = true, length = 200)
    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String value) {
        this.orgName = value;
    }

    @Column(name = "device_type", unique = false, nullable = false, insertable = true, updatable = true, length = 20)
    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String value) {
        this.deviceType = value;
    }

    @Column(name = "purchase_num", unique = false, nullable = true, insertable = true, updatable = true, length = 10)
    public Integer getPurchaseNum() {
        return this.purchaseNum;
    }

    public void setPurchaseNum(Integer value) {
        this.purchaseNum = value;
    }

    @Column(name = "origin_place", unique = false, nullable = false, insertable = true, updatable = true, length = 20)
    public String getOriginPlace() {
        return this.originPlace;
    }

    public void setOriginPlace(String value) {
        this.originPlace = value;
    }

    @Column(name = "manufacturer_name", unique = false, nullable = false, insertable = true, updatable = true, length = 50)
    public String getManufacturerName() {
        return this.manufacturerName;
    }

    public void setManufacturerName(String value) {
        this.manufacturerName = value;
    }

    @Column(name = "device_model", unique = false, nullable = false, insertable = true, updatable = true, length = 30)
    public String getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(String value) {
        this.deviceModel = value;
    }

    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "purchase_time", unique = false, nullable = true, insertable = true, updatable = true, length = 0)
    public Date getPurchaseTime() {
        return this.purchaseTime;
    }

    public void setPurchaseTime(Date value) {
        this.purchaseTime = value;
    }

    @Column(name = "is_new", unique = false, nullable = false, insertable = true, updatable = true, length = 2)
    public String getIsNew() {
        return this.isNew;
    }

    public void setIsNew(String value) {
        this.isNew = value;
    }

    @Column(name = "device_price", unique = false, nullable = false, insertable = true, updatable = true, length = 10)
    public double getDevicePrice() {
        return this.devicePrice;
    }

    public void setDevicePrice(double value) {
        this.devicePrice = value;
    }

    @Column(name = "year_limit", unique = false, nullable = false, insertable = true, updatable = true, length = 10)
    public Integer getYearLimit() {
        return this.yearLimit;
    }

    public void setYearLimit(Integer value) {
        this.yearLimit = value;
    }

    @Column(name = "status", unique = false, nullable = true, insertable = true, updatable = true, length = 2)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    @Column(name = "is_gps", unique = false, nullable = true, insertable = true, updatable = true, length = 2)
    public String getIsGps() {
        return this.isGps;
    }

    public void setIsGps(String value) {
        this.isGps = value;
    }

}
