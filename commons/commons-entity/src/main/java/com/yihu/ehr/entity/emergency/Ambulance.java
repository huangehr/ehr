package com.yihu.ehr.entity.emergency;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Entity - 救护车信息表
 * Created by progr1mmer on 2017/11/7.
 */
@Entity
@Table(name = "eme_ambulance")
@Access(value = AccessType.PROPERTY)
public class Ambulance {

    /**
     * 状态
     */
    public enum Status {
        /** 待命中 */
        wait,
        /** 执勤中 */
        active,
        /** 不可用 */
        down
    }
    //车牌号码
    private String id;
    //初始经度
    private double initLongitude;
    //初始纬度
    private double initLatitude;
    //归属片区
    private String district;
    //所属医院编码
    private int orgCode;
    //所属医院名称
    private String orgName;
    //司机
    private String driver;
    //随车手机号码
    private String phone;
    //状态
    private Status status;
    //百度鹰眼设备号
    private String entityName;

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

    @Column(name = "init_longitude", nullable = false)
    public double getInitLongitude() {
        return initLongitude;
    }

    public void setInitLongitude(double initLongitude) {
        this.initLongitude = initLongitude;
    }

    @Column(name = "init_latitude", nullable = false)
    public double getInitLatitude() {
        return initLatitude;
    }

    public void setInitLatitude(double initLatitude) {
        this.initLatitude = initLatitude;
    }

    @Column(name = "org_code", nullable = false)
    public int getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(int orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_name", nullable = false)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "driver", nullable = false)
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Column(name = "phone", nullable = false)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "district")
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Column(name = "entity_name")
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


}
