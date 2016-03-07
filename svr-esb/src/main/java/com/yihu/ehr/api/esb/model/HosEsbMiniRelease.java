package com.yihu.ehr.api.esb.model;// default package

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


/**
 * HosEsbMiniRelease entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hos_esb_mini_release")
@Access(value = AccessType.PROPERTY)
public class HosEsbMiniRelease implements java.io.Serializable {


    // Fields    

    private String id;
    private String systemCode;
    private String versionName;
    private Integer versionCode;
    private String file;
    private Date releaseTime;
    private String orgCode;


    // Constructors

    /**
     * default constructor
     */
    public HosEsbMiniRelease() {
    }

    /**
     * minimal constructor
     */
    public HosEsbMiniRelease(String id) {
        this.id = id;
    }

    /**
     * full constructor
     */
    public HosEsbMiniRelease(String id, String systemCode, String versionName, Integer versionCode, String file, Date releaseTime) {
        this.id = id;
        this.systemCode = systemCode;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.file = file;
        this.releaseTime = releaseTime;
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

    @Column(name = "system_code", length = 200)

    public String getSystemCode() {
        return this.systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Column(name = "version_name", length = 200)

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Column(name = "version_code", length = 200)

    public Integer getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    @Column(name = "file", length = 200)

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Column(name = "release_time", length = 0)

    public Date getReleaseTime() {
        return this.releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }


    @Column(name = "org_code", length = 0)
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}