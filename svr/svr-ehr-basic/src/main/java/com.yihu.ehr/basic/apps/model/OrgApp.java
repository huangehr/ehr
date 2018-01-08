package com.yihu.ehr.basic.apps.model;
import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "org_app", schema = "", catalog = "healtharchive")
public class OrgApp {
    private int id;
    private String orgId;
    private String orgName;
    private String appId;
    private String appName;
    private Integer status;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "org_id", nullable = false, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Basic
    @Column(name = "org_name", nullable = true, insertable = true, updatable = true)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "app_id", nullable = false, insertable = true, updatable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "app_name", nullable = true, insertable = true, updatable = true)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Basic
    @Column(name = "status", nullable = true, insertable = true, updatable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgApp orgApp = (OrgApp) o;

        if (id != orgApp.id) return false;
        if (orgId != null ? !orgId.equals(orgApp.orgId) : orgApp.orgId != null) return false;
        if (orgName != null ? !orgName.equals(orgApp.orgName) : orgApp.orgName != null) return false;
        if (appId != null ? !appId.equals(orgApp.appId) : orgApp.appId != null) return false;
        if (appName != null ? !appName.equals(orgApp.appName) : orgApp.appName != null) return false;
        if (status != null ? !status.equals(orgApp.status) : orgApp.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}