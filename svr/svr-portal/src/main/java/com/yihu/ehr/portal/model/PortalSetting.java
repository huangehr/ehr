package com.yihu.ehr.portal.model;

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
@Table(name = "portal_setting")
@Access(value = AccessType.FIELD)
public class PortalSetting {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private String id;
    @Column(name = "org_id", unique = true, nullable = false)
    private String orgId;
    @Column(name = "app_id", unique = true, nullable = false)
    private String appId;
    @Column(name = "status", unique = true, nullable = false)
    private int status;
    @Column(name = "column_uri", unique = true, nullable = false)
    private String columnUri;
    @Column(name = "column_name", unique = true, nullable = false)
    private String columnName;
    @Column(name = "column_request_type", unique = true, nullable = false)
    private int columnRequestType;
    @Column(name = "app_api_id", unique = true, nullable = false)
    private String appApiId;

    public PortalSetting() {}

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getColumnUri() {
        return columnUri;
    }

    public void setColumnUri(String columnUri) {
        this.columnUri = columnUri;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnRequestType() {
        return columnRequestType;
    }

    public void setColumnRequestType(int columnRequestType) {
        this.columnRequestType = columnRequestType;
    }

    public String getAppApiId() {
        return appApiId;
    }

    public void setAppApiId(String appApiId) {
        this.appApiId = appApiId;
    }
}