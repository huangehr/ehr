package com.yihu.ehr.apps.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "user_app")
@Access(value = AccessType.FIELD)
public class UserApp {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private String id;
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;
    @Column(name = "org_id", unique = true, nullable = false)
    private String orgId;
    @Column(name = "org_name", unique = true, nullable = false)
    private String orgName;
    @Column(name = "app_id", unique = true, nullable = false)
    private String appId;
    @Column(name = "app_name", unique = true, nullable = false)
    private String appName;
    @Column(name = "status", unique = true, nullable = false)
    private String status;

    public UserApp() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}