package com.yihu.ehr.analysis.model;

import org.json.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class OperatorDataModel extends DataModel implements Serializable {

    private String responseTime;
    private String url;
    private String params;

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

    public static OperatorDataModel getByJsonObject(JSONObject jsonObject) throws Exception {
        OperatorDataModel operatorDataModel = new OperatorDataModel();
        try {
            operatorDataModel.setLogType(String.valueOf(jsonObject.get("logType")));
            operatorDataModel.setCaller(jsonObject.getString("caller"));
            operatorDataModel.setTime(jsonObject.getString("time"));

            JSONObject chlidren = jsonObject.getJSONObject("data");
            operatorDataModel.setResponseTime(chlidren.get("responseTime").toString());
            operatorDataModel.setUrl(chlidren.getString("url"));
            operatorDataModel.setParams(chlidren.getString("params"));

        } catch (Exception e) {
            throw new Exception("格式错误");
        }
        return operatorDataModel;
    }
}
