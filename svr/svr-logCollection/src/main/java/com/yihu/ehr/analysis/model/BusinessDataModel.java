package com.yihu.ehr.analysis.model;

import com.yihu.ehr.analysis.service.AppFeatureService;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 * <p>
 * * 数据对象的公工接口
 * // 业务日志
 * 0 consult // 咨询
 * 1 guidance  // 指导
 * 2 article  // 健康教育
 * 3 followup  // 随访
 * 4 appointment // 预约
 * 5 label // 标签
 * 6 register  // 注册
 * 7 archive // 健康档案
 * {
     * time:"" 时间
     * ,logType:2 日志类型
     * ,caller:"" 调用者
     * ,data:{
         * ,businessType:""  业务类型
         * ,patient:"" 居民
         * ,data:{} 业务数据
     * } 数据
 * }
 */
@Document
@Component
public class BusinessDataModel extends DataModel implements Serializable {

    @Autowired
    private AppFeatureService appFeatureService;
    private String data;
    private String businessType;
    private String patient;
    private String url;
    private String responseTime;
    private String responseCode;
    private String response;
    private String appKey;
    private String operation;
    private String function;


    public BusinessDataModel getByJsonObject(JSONObject jsonObject) throws Exception {
        BusinessDataModel businessDataModel = new BusinessDataModel();
        try {
            businessDataModel.setLogType(String.valueOf(jsonObject.get("logType")));
            businessDataModel.setCaller(jsonObject.getString("caller"));
            businessDataModel.setTime(jsonObject.getString("time"));

            JSONObject chlidren = jsonObject.getJSONObject("data");
            businessDataModel.setPatient(chlidren.getString("patient"));
            businessDataModel.setUrl(chlidren.getString("url"));
            businessDataModel.setResponseTime(String.valueOf(chlidren.getInt("responseTime")));
            businessDataModel.setResponseCode(String.valueOf(chlidren.getInt("responseCode")));
            businessDataModel.setResponse(chlidren.getString("response"));
            businessDataModel.setAppKey(chlidren.getString("appKey"));

            String url = chlidren.getString("url");
            if( ! StringUtils.isEmpty(url) ){
                String a = url.substring(url.lastIndexOf(":"));
                String b = a.substring(a.indexOf("/") + 1);
                String urlApi = b.substring(b.indexOf("/") + 1);
                Object obj = appFeatureService.appFeatureFindUrl(urlApi);
                Map<String,String> map = new HashMap<>();
                if(obj != null){
                    map = appFeatureService.getOperatPageName(obj);
                }
                if(map != null && map.size() > 0){
                    businessDataModel.setOperation(map.get("operation"));
                    businessDataModel.setFunction(map.get("function"));
                }
            }


        } catch (Exception e) {
            System.out.println("格式错误"+ e.getMessage());
            throw new Exception("格式错误");
        }
        return businessDataModel;
    }

    public static void main(String[] args){
        //http://172.19.103.37:8080/esb/flow/initial
        String api = "http://172.19.103.37:8080/esb/flow/initial";
        String a = api.substring(api.lastIndexOf(":"));
        System.out.println(a);
        String b = a.substring(a.indexOf("/") + 1);
        System.out.println(b);


        System.out.println(b.substring(b.indexOf("/")+1));
    }




    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
