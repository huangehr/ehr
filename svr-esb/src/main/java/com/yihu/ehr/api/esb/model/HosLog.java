package com.yihu.ehr.api.esb.model;// default package

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


/**
 * HosLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hos_log")
public class HosLog implements java.io.Serializable {


    // Fields    

    private String id;
    private String orgCode;
    private String ip;
    private String filePath;
    private String uploadTime;


    // Constructors

    /**
     * default constructor
     */
    public HosLog() {
    }

    /**
     * minimal constructor
     */
    public HosLog(String id) {
        this.id = id;
    }

    /**
     * full constructor
     */
    public HosLog(String id, String orgCode, String ip, String filePath, String uploadTime) {
        this.id = id;
        this.orgCode = orgCode;
        this.ip = ip;
        this.filePath = filePath;
        this.uploadTime = uploadTime;
    }


    // Property accessors
    @Id

    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    @Column(name = "id", unique = true, nullable = false, length = 64)

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "org_code", length = 200)

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "ip", length = 50)

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "file_path", length = 2000)

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "upload_time", length = 100)

    public String getUploadTime() {
        return this.uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }


}