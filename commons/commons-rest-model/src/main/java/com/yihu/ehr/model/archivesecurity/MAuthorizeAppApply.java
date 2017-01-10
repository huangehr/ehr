package com.yihu.ehr.model.archivesecurity;

import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public class MAuthorizeAppApply {

    long id;
    String appId;
    String userId;
    int authorizeToken;
    int status;
    Date authorizeTime;
    Date validTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAuthorizeToken() {
        return authorizeToken;
    }

    public void setAuthorizeToken(int authorizeToken) {
        this.authorizeToken = authorizeToken;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getAuthorizeTime() {
        return authorizeTime;
    }

    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }
}