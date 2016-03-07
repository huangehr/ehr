package com.yihu.ehr.api.esb.model;// default package

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


/**
 * HosEsbMiniInstallLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hos_esb_mini_install_log")
public class HosEsbMiniInstallLog implements java.io.Serializable {


    // Fields

    private String id;
    private String orgCode;
    private String systemCode;
    private String currentVersionName;
    private String currentVersionCode;
    private Date installTime;


    // Constructors

    /**
     * default constructor
     */
    public HosEsbMiniInstallLog() {
    }

    /**
     * minimal constructor
     */
    public HosEsbMiniInstallLog(String id) {
        this.id = id;
    }

    /**
     * full constructor
     */
    public HosEsbMiniInstallLog(String id, String orgCode, String systemCode, String currentVersionName, String currentVersionCode, Timestamp installTime) {
        this.id = id;
        this.orgCode = orgCode;
        this.systemCode = systemCode;
        this.currentVersionName = currentVersionName;
        this.currentVersionCode = currentVersionCode;
        this.installTime = installTime;
    }


    // Property accessors
    @Id
    @Column(name = "id", unique = true, nullable = false, length = 64)
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "org_code", length = 64)
    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "system_code", length = 64)
    public String getSystemCode() {
        return this.systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Column(name = "current_version_name", length = 200)
    public String getCurrentVersionName() {
        return this.currentVersionName;
    }

    public void setCurrentVersionName(String currentVersionName) {
        this.currentVersionName = currentVersionName;
    }

    @Column(name = "current_version_code", length = 200)
    public String getCurrentVersionCode() {
        return this.currentVersionCode;
    }

    public void setCurrentVersionCode(String currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }

    @Column(name = "install_time", length = 0)
    public Date getInstallTime() {
        return this.installTime;
    }

    public void setInstallTime(Date installTime) {
        this.installTime = installTime;
    }


}