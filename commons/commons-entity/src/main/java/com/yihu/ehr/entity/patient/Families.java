package com.yihu.ehr.entity.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
@Entity
@Table(name = "families")
@Access(value = AccessType.PROPERTY)
public class Families {

    private String id;
    private String addressId;
    private String creator;
    private Date createDate;
    private String householderIdCardNo;
    private int status;
    private String telephone;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "address",  nullable = true)
    public String getAddressId() {
        return addressId;
    }
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    @Column(name = "creator",  nullable = true)
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "create_date",  nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "householder_id_card_no",  nullable = false)
    public String getHouseholderIdCardNo() {
        return householderIdCardNo;
    }
    public void setHouseholderIdCardNo(String householderIdCardNo) {
        this.householderIdCardNo = householderIdCardNo;
    }

    @Column(name = "status",  nullable = false)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name = "telephone",  nullable = true)
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}