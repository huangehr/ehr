package com.yihu.ehr.agModel.app;

/**
 * Created by yww on 2016/2/23.
 */
public class AppModel {
    private String id;
    private String name;
    private String secret;
    private String url;
    private String outUrl;
    private String catalog;
    private String status;
    private String description;
    private String org;
    private String code;
    private int sourceType;
    private String icon;
    private int releaseFlag;
    private String manageType; // 管理类型，dictId=94

    private String statusName;
    private String catalogName;
    private String resourceNames;
    private String sourceTypeName;
    private String orgName;
    private String role;
    private String roleJson;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogName() {
        return catalogName;
    }
    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getOutUrl() {
        return outUrl;
    }
    public void setOutUrl(String outUrl) {
        this.outUrl = outUrl;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getResourceNames() {
        return resourceNames;
    }
    public void setResourceNames(String resourceNames) {
        this.resourceNames = resourceNames;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getReleaseFlag() {
        return releaseFlag;
    }
    public void setReleaseFlag(int releaseFlag) {
        this.releaseFlag = releaseFlag;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleJson() {
        return roleJson;
    }
    public void setRoleJson(String roleJson) {
        this.roleJson = roleJson;
    }

    public int getSourceType() {
        return sourceType;
    }
    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceTypeName() {
        return sourceTypeName;
    }
    public void setSourceTypeName(String sourceTypeName) {
        this.sourceTypeName = sourceTypeName;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getManageType() {
        return manageType;
    }
    public void setManageType(String manageType) {
        this.manageType = manageType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
