package com.yihu.ehr.model.portal;

import java.io.Serializable;

/**
 *  可下载资源model
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/17.
 */
public class MItResource implements Serializable {

    private int id;
    private String name;
    private String version;
    private String platformType;
    private String pakageType;
    private String description;
    private String developLan;
    private String validateType;
    private Integer status;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
