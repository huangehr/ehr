package com.yihu.ehr.dao.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Entity
@Table(name = "sc_authorize_app_apply")
@Access(value = AccessType.PROPERTY)
public class AuthorizeAppApply {

    long id;
    String appId;
    String userId;
    int authorizeToken;
    int status;
    Date authorizeTime;
    Date validTime;

    public AuthorizeAppApply() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "app_id", nullable = false)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "user_id", nullable = false)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "authorize_token", nullable = false)
    public int getAuthorizeToken() {
        return authorizeToken;
    }
    public void setAuthorizeToken(int authorizeToken) {
        this.authorizeToken = authorizeToken;
    }

    @Column(name = "status", nullable = false)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name = "authorize_time", nullable = false)
    public Date getAuthorizeTime() {
        return authorizeTime;
    }
    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    @Column(name = "valid_time", nullable = false)
    public Date getValidTime() {
        return validTime;
    }
    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }
}