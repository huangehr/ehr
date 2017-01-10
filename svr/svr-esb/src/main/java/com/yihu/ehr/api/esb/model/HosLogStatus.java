package com.yihu.ehr.api.esb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


/**
 * HosLogStatus entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hos_log_status")
public class HosLogStatus implements java.io.Serializable {


    // Fields
    private String id;
    private String systemCode;
    private String orgCode;
    private String status;


    // Constructors

    /**
     * default constructor
     */
    public HosLogStatus() {
    }


    /**
     * full constructor
     */
    public HosLogStatus(String systemCode, String orgCode, String status) {
        this.systemCode = systemCode;
        this.orgCode = orgCode;
        this.status = status;
    }


    // Property accessors
    @GenericGenerator(name = "generator", strategy = "uuid")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id", unique = true, nullable = false, length = 64)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "system_code", length = 64)
    public String getSystemCode() {
        return this.systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Column(name = "org_code", length = 64)
    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "status", length = 10)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}