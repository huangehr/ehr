package com.yihu.ehr.basic.apps.model;

import javax.persistence.*;

/**
 * APP_api对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

@Entity
@Table(name = "apps_api")
@Access(value = AccessType.PROPERTY)
public class AppApi {

    public enum TransferProtocol {
        http,
        https
    }

    private int id;
    private String appId; //所属应用ID
    private String name; //Api名称
    private String description; //Api描述
    private String type; //类别 1 - API;  2 - 应用; 3 - SDK
    private String method; //请求方式 0 - GET;  1 - POST;  2 - DELETE;  3 - PUT
    private String protocol; //对外协议 0 - WebService;  1 - RESTful
    private String innerProtocol; //对内协议 0 - WebService;  1 - RESTful
    private TransferProtocol transferProtocol; //0 - HTTP;  1 - HTTPS
    private String version; //版本
    private int parentId; //该字段弃用
    private String activityType; //生失效标识 0 - 无效果;  1 - 有效
    private String parameterDemo; //请求参数示例
    private String responseDemo; //返回结果示例
    private String errorDemo; //接口请求错误示例
    private String openLevel; //开放程度 0 - 私有;  1 - 公开
    private String auditLevel; //审计程度 0 - 不审计;  1 - 审计
    private String methodName; //对外网关接口方法名
    private String microServiceUri; //对外接口实际IP地址和端口
    private String msMethodName; //内部实际微服务中方法名
    private String microServiceName; //内部实际微服务名称
    private Integer category; //业务类别
    private String categoryName; //业务类别名称

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "app_id", nullable = true)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
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

    @Column(name = "type", nullable = true)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "method", nullable = true)
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    @Column(name = "protocol")
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Column(name = "inner_protocol")
    public String getInnerProtocol() {
        return innerProtocol;
    }
    public void setInnerProtocol(String innerProtocol) {
        this.innerProtocol = innerProtocol;
    }

    @Column(name = "transfer_protocol")
    public TransferProtocol getTransferProtocol() {
        return transferProtocol;
    }
    public void setTransferProtocol(TransferProtocol transferProtocol) {
        this.transferProtocol = transferProtocol;
    }

    @Column(name = "version", nullable = true)
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "parent_id", nullable = true)
    public int getParentId() {
        return parentId;
    }
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Column(name = "parameter_demo", nullable = true)
    public String getParameterDemo() {
        return parameterDemo;
    }
    public void setParameterDemo(String parameterDemo) {
        this.parameterDemo = parameterDemo;
    }

    @Column(name = "activity_type", nullable = true)
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    @Column(name = "response_demo", nullable = true)
    public String getResponseDemo() {
        return responseDemo;
    }
    public void setResponseDemo(String responseDemo) {
        this.responseDemo = responseDemo;
    }

    @Column(name = "error_demo")
    public String getErrorDemo() {
        return errorDemo;
    }

    public void setErrorDemo(String errorDemo) {
        this.errorDemo = errorDemo;
    }

    @Column(name = "open_level", nullable = true)
    public String getOpenLevel() {
        return openLevel;
    }
    public void setOpenLevel(String openLevel) {
        this.openLevel = openLevel;
    }

    @Column(name = "audit_level", nullable = true)
    public String getAuditLevel() {
        return auditLevel;
    }
    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    @Column(name = "method_name", nullable = true)
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Column(name = "micro_service_url", nullable = true)
    public String getMicroServiceUri() {
        return microServiceUri;
    }
    public void setMicroServiceUri(String microServiceUri) {
        this.microServiceUri = microServiceUri;
    }

    @Column(name = "ms_method_name", nullable = true)
    public String getMsMethodName() {
        return msMethodName;
    }
    public void setMsMethodName(String msMethodName) {
        this.msMethodName = msMethodName;
    }

    @Column(name = "micro_service_name", nullable = true)
    public String getMicroServiceName() {
        return microServiceName;
    }
    public void setMicroServiceName(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    @Column(name = "category")
    public Integer getCategory() {
        return category;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }

    @Transient
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}