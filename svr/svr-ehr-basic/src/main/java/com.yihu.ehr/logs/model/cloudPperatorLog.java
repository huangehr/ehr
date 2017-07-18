package com.yihu.ehr.logs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cloudPperatorLog")
public class cloudPperatorLog {
    @Id
    private String id;              //uuid
    private String caller;         //业务操作者
    private String time;           //操作时间
    private String responseTime;       //响应时间
    private String params;          //参数
    private String data;          //相关信息

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
