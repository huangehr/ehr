package com.yihu.ehr.apps.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * APP_api对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

@Entity
@Table(name = "apps_api")
public class AppApi {

    private int id;
    private String appId;
    private String name;
    private String description;
    private String type;
    private String method;
    private String protocol;
    private String version;
    private int parentId;
    private String parameterDemo;
    private String activityType;
    private String responseDemo;
    private String openLevel;
    private String auditLevel;
    private String methodName;

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
    @Column(name = "protocol", nullable = true)
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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
}