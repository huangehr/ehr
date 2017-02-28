package com.yihu.ehr.model.portal;

import java.io.Serializable;

/**
 * Created by yeshijie on 2017/2/21.
 */
public class MPortalSetting implements Serializable {

    private Long id;
    private String orgId;
    private String appId;
    private String columnUri;
    private String columnName;
    private Integer columnRequestType;
    private Integer appApiId;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getColumnRequestType() {
        return columnRequestType;
    }

    public void setColumnRequestType(Integer columnRequestType) {
        this.columnRequestType = columnRequestType;
    }

    public Integer getAppApiId() {
        return appApiId;
    }

    public void setAppApiId(Integer appApiId) {
        this.appApiId = appApiId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
