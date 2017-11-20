package com.yihu.ehr.entity.emergency;


import com.yihu.ehr.entity.BaseAssignedEntity;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;


/**
 * Entity - 救护车信息表
 * Created by progr1mmer on 2017/11/7.
 */
@Entity
@Table(name = "eme_ambulance")
@Access(value = AccessType.PROPERTY)
public class Ambulance extends BaseAssignedEntity {

    /**
     * 状态
     */
    public enum Status {
        /** 待命中 */
        wait,
        /** 前往中 */
        onWay,
        /** 抵达 */
        arrival,
        /** 返程中 */
        back,
        /** 异常 */
        down
    }

    //初始经度
    private double initLongitude;
    //初始纬度
    private double initLatitude;
    //归属片区
    private String district;
    //所属医院编码
    private String orgCode;
    //所属医院名称
    private String orgName;
    //随车手机号码
    private String phone;
    //状态
    private Status status;
    //百度鹰眼设备号
    private String entityName;

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
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_name", nullable = false)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "phone", nullable = false)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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
