package com.yihu.ehr.analysis.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.analysis.service.AppService;
import com.yihu.ehr.model.app.MApp;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/9.
 * {
 * time:"" 时间
 * ,logType:1 日志类型
 * ,caller:"" 调用者
 * ,data:{
 * responseTime:"" 响应时间
 * ,url:"" 接口URL
 * ,params:{} 参数
 * } 数据
 * }
 */
@Component
public class OperatorDataModel extends DataModel implements Serializable {

    @Autowired
    private AppService appService;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX")
    @CreatedDate
    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ssXX")
    private Date responseTime;
    private String responseCode;
    private String response;
    private String api;
    private String appKey;
    private String appName;
    private String url;
    private String params;

    public  OperatorDataModel getByJsonObject(JSONObject jsonObject) throws Exception {
        OperatorDataModel operatorDataModel = new OperatorDataModel();
        try {
            operatorDataModel.setLogType(String.valueOf(jsonObject.get("logType")));
            operatorDataModel.setCaller(jsonObject.getString("caller"));
            operatorDataModel.setTime(changeTime(jsonObject.getString("time")));

            JSONObject chlidren = jsonObject.getJSONObject("data");
            operatorDataModel.setResponseTime(changeTime(chlidren.get("responseTime").toString()));
            operatorDataModel.setResponseCode(chlidren.getString("responseCode"));
            operatorDataModel.setResponse(chlidren.getString("response"));
            operatorDataModel.setUrl(chlidren.getString("url"));

            JSONObject paramsChild = chlidren.getJSONObject("params");
            operatorDataModel.setApi(paramsChild.getString("api"));
            operatorDataModel.setAppKey(paramsChild.getString("appKey"));
            if( ! StringUtils.isEmpty(paramsChild.getString("appKey") ) ){
                MApp app = appService.getApp(paramsChild.getString("appKey"));
                if(app != null){
                    operatorDataModel.setAppName(app.getName());
                }
            }
            operatorDataModel.setParams(paramsChild.getString("param"));

        } catch (Exception e) {
            throw new Exception("格式错误");
        }
        return operatorDataModel;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
