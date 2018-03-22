package com.yihu.ehr.model.family;// default package

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author lyr
 * @version 1.0
 * @updated 21-4月-2016 09:50:02
 */
public class MFamilies {

    private String id;
    private String addressId;
    private String creator;
    private Date createDate;
    private String householderIdCardNo;
    private int status;
    private String telephone;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getHouseholderIdCardNo() {
        return householderIdCardNo;
    }
    public void setHouseholderIdCardNo(String householderIdCardNo) {
        this.householderIdCardNo = householderIdCardNo;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}