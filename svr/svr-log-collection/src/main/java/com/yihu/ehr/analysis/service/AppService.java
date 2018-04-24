package com.yihu.ehr.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.util.http.HttpResponse;
import com.yihu.ehr.util.http.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by janseny on 2017/7/18.
 */
@Service
public class AppService {

    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 根据app_id 获取 APP信息
     *
     * @param appId
     * @return
     */
    public MApp getApp(String appId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        String rqUrl = "/app";
        params.put("app_id", appId);
        HttpResponse  httpResponse = HttpUtils.doGet(comUrl + rqUrl, params, null,  username, password);
        MApp detailModel = objectMapper.readValue(httpResponse.getContent(), MApp.class);
        return detailModel;
    }


}
