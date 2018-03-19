package com.yihu.ehr.model.app;

import java.util.List;

/**
 * model - 开放平台api信息
 * Created by progr1mmer on 2018/1/24.
 */
public class OpenAppApi {

    private int id;
    private String appId;
    private String name;
    private String description;
    private String type;
    private String method;
    private String protocol;
    private String version;
    private int parentId; //该字段弃用
    private String parameterDemo; //请求参数示例
    private String activityType;
    private String responseDemo; //返回结果示例
    private String openLevel;
    private String auditLevel;
    private String methodName; //对外网关接口方法名
    private String microServiceUri; //对外接口实际IP地址和端口
    private String msMethodName; //内部实际微服务中方法名
    private String microServiceName; //内部实际微服务名称
    private Integer category; //业务类别

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

    public String getMicroServiceUri() {
        return microServiceUri;
    }

    public void setMicroServiceUri(String microServiceUri) {
        this.microServiceUri = microServiceUri;
    }

    public String getMsMethodName() {
        return msMethodName;
    }

    public void setMsMethodName(String msMethodName) {
        this.msMethodName = msMethodName;
    }

    public String getMicroServiceName() {
        return microServiceName;
    }

    public void setMicroServiceName(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

}
