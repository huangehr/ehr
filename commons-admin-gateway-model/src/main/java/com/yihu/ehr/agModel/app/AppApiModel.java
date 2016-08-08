package com.yihu.ehr.agModel.app;

/**
 * APP_api对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */
public class AppApiModel {

    private int id;
    private String appId;
    private String name;
    private String description;
    private String type;
    private String typeName;
    private String method;
    private String protocol;
    private String version;
    private int parentId;
    private String parameterDemo;
    private String activityType;
    private String activityTypeName;
    private String responseDemo;
    private String openLevel;
    private String auditLevel;
    private String openLevelName;
    private String auditLevelName;
    private String methodName;

    public String getOpenLevelName() {
        return openLevelName;
    }

    public void setOpenLevelName(String openLevelName) {
        this.openLevelName = openLevelName;
    }

    public String getAuditLevelName() {
        return auditLevelName;
    }

    public void setAuditLevelName(String auditLevelName) {
        this.auditLevelName = auditLevelName;
    }

    /**
     * 界面上适配选中是否适配用做界面展示，代表是否以及被适配
     */
    private Boolean ischecked;

    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Boolean getIschecked() {
        return ischecked;
    }

    public void setIschecked(Boolean ischecked) {
        this.ischecked = ischecked;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getActivityTypeName() {
        return activityTypeName;
    }

    public void setActivityTypeName(String activityTypeName) {
        this.activityTypeName = activityTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParameterDemo() {
        return parameterDemo;
    }

    public void setParameterDemo(String parameterDemo) {
        this.parameterDemo = parameterDemo;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getResponseDemo() {
        return responseDemo;
    }

    public void setResponseDemo(String responseDemo) {
        this.responseDemo = responseDemo;
    }

    public String getOpenLevel() {
        return openLevel;
    }

    public void setOpenLevel(String openLevel) {
        this.openLevel = openLevel;
    }

    public String getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}