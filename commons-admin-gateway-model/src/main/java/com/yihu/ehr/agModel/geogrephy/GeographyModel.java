package com.yihu.ehr.agModel.geogrephy;


import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @author Sand
 * @version 1.0
 * @updated 25-05-2015 19:56:00
 */
public class GeographyModel implements Serializable {

    private String id;

    private String country;

    private String province;

    private String city;

    private String district;

    private String town;

    private String street;

    private String extra;

    private String postalCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String fullAddress() {
        String addressStr="";
        if (!StringUtils.isEmpty(province)){
            addressStr += province;
            if (!"".equals(city)) {
                if (!province.equals(city)){
                    addressStr += city;
                }
            }
        }
        if (!StringUtils.isEmpty(district)){
            addressStr += district;
        }
        if (!StringUtils.isEmpty(town)){
            addressStr += town;
        }
        if (!StringUtils.isEmpty(street)){
            addressStr += street;
        }
        if (!StringUtils.isEmpty(extra)){
            addressStr += extra;
        }
        return addressStr;
    }

    public boolean nullAddress() {
        return
                StringUtils.isEmpty(province)
                        && StringUtils.isEmpty(city)
                        && StringUtils.isEmpty(district)
                        && StringUtils.isEmpty(town)
                        && StringUtils.isEmpty(street)
                        && StringUtils.isEmpty(extra);
    }
}