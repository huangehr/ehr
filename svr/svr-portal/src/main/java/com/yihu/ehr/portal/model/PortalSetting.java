package com.yihu.ehr.portal.model;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "portal_setting", schema = "", catalog = "healtharchive")
public class PortalSetting {
    private int id;
    private String orgId;
    private String appId;
    private String columnUri;
    private String columnName;
    private Integer columnRequestType;
    private Integer appApiId;
    private String status;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "org_id", nullable = true, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Basic
    @Column(name = "app_id", nullable = true, insertable = true, updatable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "column_uri", nullable = true, insertable = true, updatable = true)
    public String getColumnUri() {
        return columnUri;
    }

    public void setColumnUri(String columnUri) {
        this.columnUri = columnUri;
    }

    @Basic
    @Column(name = "column_name", nullable = true, insertable = true, updatable = true)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Basic
    @Column(name = "column_request_type", nullable = true, insertable = true, updatable = true)
    public Integer getColumnRequestType() {
        return columnRequestType;
    }

    public void setColumnRequestType(Integer columnRequestType) {
        this.columnRequestType = columnRequestType;
    }

    @Basic
    @Column(name = "app_api_id", nullable = true, insertable = true, updatable = true)
    public Integer getAppApiId() {
        return appApiId;
    }

    public void setAppApiId(Integer appApiId) {
        this.appApiId = appApiId;
    }

    @Basic
    @Column(name = "status", nullable = true, insertable = true, updatable = true)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalSetting that = (PortalSetting) o;

        if (id != that.id) return false;
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) return false;
        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (columnUri != null ? !columnUri.equals(that.columnUri) : that.columnUri != null) return false;
        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
        if (columnRequestType != null ? !columnRequestType.equals(that.columnRequestType) : that.columnRequestType != null)
            return false;
        if (appApiId != null ? !appApiId.equals(that.appApiId) : that.appApiId != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (columnUri != null ? columnUri.hashCode() : 0);
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        result = 31 * result + (columnRequestType != null ? columnRequestType.hashCode() : 0);
        result = 31 * result + (appApiId != null ? appApiId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
