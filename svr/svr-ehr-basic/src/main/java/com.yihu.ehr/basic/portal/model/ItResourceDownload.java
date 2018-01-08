package com.yihu.ehr.basic.portal.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by janseny on 2017/3/11.
 */
@Entity
@Table(name = "it_resource_download", schema = "", catalog = "healtharchive")
public class ItResourceDownload {
    private Long id;
    private String name;
    private String version;
    private String platformType;
    private String pakageType;
    private String description;
    private String developLan;
    private String validateType;
    private String picUrl;
    private String url;
    private String iosQrCodeUrl;
    private String androidQrCodeUrl;
    private String uploadUser;
    private Date uploadTime;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true)
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @Basic
    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    public String getVersion() {return version;}

    public void setVersion(String version) {this.version = version;}

    @Basic
    @Column(name = "platform_type", nullable = true, insertable = true, updatable = true)
    public String getPlatformType() {return platformType;}

    public void setPlatformType(String platformType) {this.platformType = platformType;}

    @Basic
    @Column(name = "pakage_type", nullable = true, insertable = true, updatable = true)
    public String getPakageType() {return pakageType;}

    public void setPakageType(String pakageType) {this.pakageType = pakageType;}

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true)
    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    @Basic
    @Column(name = "develop_lan", nullable = true, insertable = true, updatable = true)
    public String getDevelopLan() {return developLan;}

    public void setDevelopLan(String developLan) {this.developLan = developLan;}

    @Basic
    @Column(name = "validate_type", nullable = true, insertable = true, updatable = true)
    public String getValidateType() {return validateType;}

    public void setValidateType(String validateType) {this.validateType = validateType;}

    @Basic
    @Column(name = "pic_url", nullable = true, insertable = true, updatable = true)
    public String getPicUrl() {return picUrl;}

    public void setPicUrl(String picUrl) {this.picUrl = picUrl;}

    @Basic
    @Column(name = "url", nullable = true, insertable = true, updatable = true)
    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    @Basic
    @Column(name = "ios_qr_code_url", nullable = true, insertable = true, updatable = true)
    public String getIosQrCodeUrl() {return iosQrCodeUrl;}

    public void setIosQrCodeUrl(String iosQrCodeUrl) {this.iosQrCodeUrl = iosQrCodeUrl;}

    @Basic
    @Column(name = "android_qr_code_url", nullable = true, insertable = true, updatable = true)
    public String getAndroidQrCodeUrl() {return androidQrCodeUrl;}

    public void setAndroidQrCodeUrl(String androidQrCodeUrl) {this.androidQrCodeUrl = androidQrCodeUrl;}

    @Basic
    @Column(name = "upload_user", nullable = true, insertable = true, updatable = true)
    public String getUploadUser() {return uploadUser;}

    public void setUploadUser(String uploadUser) {this.uploadUser = uploadUser;}

    @Basic
    @Column(name = "upload_time", nullable = true, insertable = true, updatable = true)
    public Date getUploadTime() { return uploadTime;}

    public void setUploadTime(Date uploadTime) {this.uploadTime = uploadTime;}
}
