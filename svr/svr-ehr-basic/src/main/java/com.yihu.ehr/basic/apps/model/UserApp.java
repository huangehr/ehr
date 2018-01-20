package com.yihu.ehr.basic.apps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "user_app", schema = "", catalog = "healtharchive")
public class UserApp {
    private int id;
    private String userId;
    private String userName;
    private String orgId;
    private String orgName;
    private String appId;
    private String appName;
    private Integer status;
    private Integer showFlag;



    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "user_name", nullable = true, insertable = true, updatable = true)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "org_id", nullable = false, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "org_name", nullable = true, insertable = true, updatable = true)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "app_id", nullable = false, insertable = true, updatable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "app_name", nullable = true, insertable = true, updatable = true)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Column(name = "status", nullable = true, insertable = true, updatable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "show_flag", nullable = true, insertable = true, updatable = true)
    public Integer getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(Integer showFlag) {
        this.showFlag = showFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserApp userApp = (UserApp) o;

        if (id != userApp.id) return false;
        if (userId != null ? !userId.equals(userApp.userId) : userApp.userId != null) return false;
        if (userName != null ? !userName.equals(userApp.userName) : userApp.userName != null) return false;
        if (orgId != null ? !orgId.equals(userApp.orgId) : userApp.orgId != null) return false;
        if (orgName != null ? !orgName.equals(userApp.orgName) : userApp.orgName != null) return false;
        if (appId != null ? !appId.equals(userApp.appId) : userApp.appId != null) return false;
        if (appName != null ? !appName.equals(userApp.appName) : userApp.appName != null) return false;
        if (status != null ? !status.equals(userApp.status) : userApp.status != null) return false;
        if (showFlag != null ? !showFlag.equals(userApp.showFlag) : userApp.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (showFlag != null ? showFlag.hashCode() : 0);
        return result;
    }
}