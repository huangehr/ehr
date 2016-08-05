package com.yihu.ehr.medicalRecords.comom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.HttpClientUtil.HttpClientUtil;
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

    @Value("${wlyy-service.url}")
    String serviceUrl;

    @Value("${wlyy-service.platform}")
    String platform;

    @Autowired
    ObjectMapper objectMapper;

    /***
     * 单点登录接口
     */
    public WlyyResponse userSessionCheck(String id,String uid,String imei,String token) throws Exception
    {
        String url = serviceUrl + "login/third/login";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("uid", uid);
        params.put("imei", imei);
        params.put("token", token);
        params.put("platform", platform);
        String result = HttpClientUtil.doPost(url, params, null, null);

        return objectMapper.readValue(result, WlyyResponse.class);
    }

    /***
     * 获取医生信息
     */
    public WlyyResponse queryDoctorInfoByID (String code) throws Exception
    {
        String url = serviceUrl + "doctor/baseinfo";
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = HttpClientUtil.doPost(url, params, null, null);

        return objectMapper.readValue(result, WlyyResponse.class);
    }

    /***
     * 获取患者信息
     */
    public WlyyResponse queryPatientInfoByID (String code) throws Exception
    {
        String url = serviceUrl + "doctor/patient_group/patient";
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        String result = HttpClientUtil.doPost(url, params, null, null);

        return objectMapper.readValue(result, WlyyResponse.class);
    }


}
