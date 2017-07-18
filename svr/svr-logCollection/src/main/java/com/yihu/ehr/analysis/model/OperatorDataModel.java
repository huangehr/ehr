package com.yihu.ehr.analysis.model;

import com.yihu.ehr.analysis.service.AppFeatureService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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
@Document
@Component
public class OperatorDataModel extends DataModel implements Serializable {

    @Autowired
    private AppFeatureService appFeatureService;

    private String responseTime;
    private String responseCode;
    private String response;
    private String api;
    private String appKey;
    private String url;
    private String params;

    public  OperatorDataModel getByJsonObject(JSONObject jsonObject) throws Exception {
        OperatorDataModel operatorDataModel = new OperatorDataModel();
        try {
            operatorDataModel.setLogType(String.valueOf(jsonObject.get("logType")));
            operatorDataModel.setCaller(jsonObject.getString("caller"));
            operatorDataModel.setTime(jsonObject.getString("time"));

            JSONObject chlidren = jsonObject.getJSONObject("data");
            operatorDataModel.setResponseTime(chlidren.get("responseTime").toString());
            operatorDataModel.setResponseCode(chlidren.getString("responseCode"));
            operatorDataModel.setResponse(chlidren.getString("response"));
            operatorDataModel.setApi(chlidren.getString("api"));
            operatorDataModel.setAppKey(chlidren.getString("appKey"));
            operatorDataModel.setUrl(chlidren.getString("url"));
            appFeatureService.appFeatureFindUrl(chlidren.getString("url"));
            operatorDataModel.setParams(chlidren.getString("params"));

        } catch (Exception e) {
            throw new Exception("格式错误");
        }
        return operatorDataModel;
    }


    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
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


}
