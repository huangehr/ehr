package com.yihu.ehr.geography.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Sand
 * @version 1.0
 * @updated 25-05-2015 19:56:00
 */
@Entity
@Table(name = "addresses")
@Access(value = AccessType.PROPERTY)
public class Geography {
    private String id;
    private String country;
    private String province;
    private String city;
    private String district;
    private String town;
    private String street;
    private String extra;
    private String postalCode;
    public Geography() {

    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    } public void setId(String id) {
        this.id = id;
    }

    @Column(name = "country",  nullable = true)
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "province",  nullable = true)
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "city",  nullable = true)
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "district",  nullable = true)
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    @Column(name = "town",  nullable = true)
    public String getTown() {
        return town;
    }
    public void setTown(String town) {
        this.town = town;
    }

    @Column(name = "street",  nullable = true)
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "extra",  nullable = true)
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Column(name = "postal_code",  nullable = true)
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

//
//
//
//    public String getCanonicalAddress() {
//        String address = "";
//        //if (country != null) address += country;
//        if (province != null){
//            address += province;
//            if (city != null) {
//                if (!province.equals(city)){
//                    address += city;
//                }
//            }
//        }
//        if (district != null) address += district;
//        if (town != null) address += town;
//        if (street != null) address += street;
//        if (extra != null) address += extra;
//
//        return address;
//    }
//
//    public boolean isNullAddress() {
//        return province == null && city == null && district == null && town == null && country == null && street == null;
//    }
//
//    public boolean isAvailable(){
//        if(country == null && province == null) return false;
//        if(province == null && city != null) return false;
//        if(city == null && district != null) return false;
//        return true;
//    }
//
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Address address = (Address) o;
//
//        //if (id != address.id) return false;
//        if (country != null ? !country.equals(address.country) : address.country != null) return false;
//        if (province != null ? !province.equals(address.province) : address.province != null) return false;
//        if (city != null ? !city.equals(address.city) : address.city != null) return false;
//        if (district != null ? !district.equals(address.district) : address.district != null) return false;
//        if (town != null ? !town.equals(address.town) : address.town != null) return false;
//        if (street != null ? !street.equals(address.street) : address.street != null) return false;
//        if (extra != null ? !extra.equals(address.extra) : address.extra != null) return false;
//        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null) return false;
//
//        return true;
//    }
//
//    public int hashCode() {
//        int result = 1;
//        result = 31 * result + (country != null ? country.hashCode() : 0);
//        result = 31 * result + (province != null ? province.hashCode() : 0);
//        result = 31 * result + (city != null ? city.hashCode() : 0);
//        result = 31 * result + (district != null ? district.hashCode() : 0);
//        result = 31 * result + (town != null ? town.hashCode() : 0);
//        result = 31 * result + (street != null ? street.hashCode() : 0);
//        result = 31 * result + (extra != null ? extra.hashCode() : 0);
//        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
//        return result;
//    }
}