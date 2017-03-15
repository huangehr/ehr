package com.yihu.ehr.agModel.portal;

/**
 * Created by zhoujie on 2017/3/11.
 */
public class PortalResourcesModel {
    private Long id;
    private String name;
    private String version;
    private String platformType;
    private String platformTypeName;
    private String pakageType;
    private String description;
    private String developLan;
    private String developLanName;
    private String validateType;
    private String picUrl;
    private String url;
    private String iosQrCodeUrl;
    private String androidQrCodeUrl;
    private String uploadUser;
    private String uploadTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getPlatformTypeName() {return platformTypeName;}

    public void setPlatformTypeName(String platformTypeName) {this.platformTypeName = platformTypeName;}

    public String getDevelopLanName() {return developLanName;}

    public void setDevelopLanName(String developLanName) {this.developLanName = developLanName;}

    public String getPakageType() {
        return pakageType;
    }

    public void setPakageType(String pakageType) {
        this.pakageType = pakageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDevelopLan() {
        return developLan;
    }

    public void setDevelopLan(String developLan) {
        this.developLan = developLan;
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIosQrCodeUrl() {
        return iosQrCodeUrl;
    }

    public void setIosQrCodeUrl(String iosQrCodeUrl) {
        this.iosQrCodeUrl = iosQrCodeUrl;
    }

    public String getAndroidQrCodeUrl() {
        return androidQrCodeUrl;
    }

    public void setAndroidQrCodeUrl(String androidQrCodeUrl) {
        this.androidQrCodeUrl = androidQrCodeUrl;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
