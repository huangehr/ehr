package com.yihu.ehr.apps.model;

import javax.persistence.*;

/**
 * apps_api_feature对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

@Entity
@Table(name = "apps_api_feature")
public class AppApiFeature {

    private int id;
    private String code;
    private String name;
    private String description;
    private String type;
    private int parentId;
    private String url;
    private String iconUrl;
    private String auditLevel;
    private String openLevel;
    private String appId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "code", nullable = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    @Column(name = "type", nullable = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "parentId", nullable = true)
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Column(name = "url", nullable = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "icon_url", nullable = true)
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Column(name = "audit_level", nullable = true)
    public String getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    @Column(name = "open_level", nullable = true)
    public String getOpenLevel() {
        return openLevel;
    }

    public void setOpenLevel(String openLevel) {
        this.openLevel = openLevel;
    }

    public String getAppId() {
        return appId;
    }

    @Column(name = "app_id", nullable = true)
    public void setAppId(String appId) {
        this.appId = appId;
    }
}