package com.yihu.ehr.medicalRecords.comom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.httpClient.HttpHelper;
import com.yihu.ehr.util.httpClient.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzp on 2016/8/4.
 * 网络医院用户账号管理接口
 */
@Service
public class WlyyService {

    @Autowired
    HttpHelper httpHelper;

    @Value("${wlyy-service.url}")
    String serviceUrl;


    @Autowired
    ObjectMapper objectMapper;

    /***
     * 单点登录接口
     */
    public WlyyResponse userSessionCheck(String json) throws Exception
    {
        String url = serviceUrl + "login/third/login";
        Map<String, Object> params = (Map<String, Object>)objectMapper.readValue(json,Map.class);
        HttpResponse response = httpHelper.post(url, params);
        if(response.getStatusCode() == 200)
        {
            return objectMapper.readValue(response.getBody(), WlyyResponse.class);
        }
        else{
            Message.error(response.getBody());
            return null;
        }
    }


    /***
     * 获取医生信息
     */
    public WlyyResponse queryDoctorInfoByID (String code,String headInfo) throws Exception
    {
        String url = serviceUrl + "doctor/baseinfo";
        Map<String, Object> head = new HashMap<>();
        head.put("User-Agent", headInfo);
        HttpResponse response = httpHelper.post(url, null,head);
        if(response.getStatusCode() == 200)
        {
            return objectMapper.readValue(response.getBody(), WlyyResponse.class);
        }
        else{
            Message.error(response.getBody());
            return null;
        }
    }

    /***
     * 获取患者信息
     */
    public WlyyResponse queryPatientInfoByID (String code,String headInfo) throws Exception
    {
        String url = serviceUrl + "doctor/sign/patient";
        Map<String, Object> params = new HashMap<>();
        params.put("patient", code);
        Map<String, Object> head = new HashMap<>();
        head.put("User-Agent", headInfo);
        HttpResponse response = httpHelper.post(url, params,head);
        if(response.getStatusCode() == 200)
        {
            return objectMapper.readValue(response.getBody(), WlyyResponse.class);
        }
        else{
            Message.error(response.getBody());
            return null;
        }
    }


}
