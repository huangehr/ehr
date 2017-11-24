package com.yihu.ehr.entity.emergency;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;

/**
 * Entity - 待命地点
 * Created by progr1mmer on 2017/11/22.
 */
@Entity
@Table(name = "eme_location")
@Access(value = AccessType.PROPERTY)
public class Location extends BaseIdentityEntity {

    // 待命经度
    private double initLongitude;
    // 待命纬度
    private double initLatitude;
    // 待命地点
    private String initAddress;
    // 归属片区
    private String district;

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

    @Column(name = "init_address")
    public String getInitAddress() {
        return initAddress;
    }

    public void setInitAddress(String initAddress) {
        this.initAddress = initAddress;
    }

    @Column(name = "district")
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
