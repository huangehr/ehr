package com.yihu.ehr.model.app;

import java.util.List;

/**
 * model - 开放平台api信息
 * Created by progr1mmer on 2018/1/24.
 */
public class OpenAppApi {

    private int id;
    private String name;
    private String description;
    private String type;
    private String method;
    private String protocol;
    private String version;
    private String openLevel;
    private String microServiceUrl;
    private String msMethodName;
    private List parameter;
    private List response;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getOpenLevel() {
        return openLevel;
    }

    public void setOpenLevel(String openLevel) {
        this.openLevel = openLevel;
    }

    public String getMicroServiceUrl() {
        return microServiceUrl;
    }

    public void setMicroServiceUrl(String microServiceUrl) {
        this.microServiceUrl = microServiceUrl;
    }

    public String getMsMethodName() {
        return msMethodName;
    }

    public void setMsMethodName(String msMethodName) {
        this.msMethodName = msMethodName;
    }

    public List getParameter() {
        return parameter;
    }

    public void setParameter(List parameter) {
        this.parameter = parameter;
    }

    public List getResponse() {
        return response;
    }

    public void setResponse(List response) {
        this.response = response;
    }
}
