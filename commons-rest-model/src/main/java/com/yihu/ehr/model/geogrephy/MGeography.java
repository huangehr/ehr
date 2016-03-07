package com.yihu.ehr.model.geogrephy;


import java.io.Serializable;
import java.util.UUID;

/**
 * @author Sand
 * @version 1.0
 * @updated 25-05-2015 19:56:00
 */
public class MGeography implements Serializable {
    private String id;
    private String country;
    private String province;
    private String city;
    private String district;
    private String town;
    private String street;
    private String extra;
    private String postalCode;

    public MGeography() {

    }


    public MGeography(String province,String city,String district,String town,String street) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.town = town;
        this.province = province;
        this.street = street;
    }

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
}