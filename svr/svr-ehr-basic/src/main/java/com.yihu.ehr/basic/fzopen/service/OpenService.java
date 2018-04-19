package com.yihu.ehr.basic.fzopen.service;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.fzgateway.open.OPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 转发福州总部开放平台接口 Service
 *
 * @author 张进军
 * @date 2018/4/14 17:39
 */
@Service
@Transactional
public class OpenService {

    @Value("${fz-gateway.clientId}")
    private String fzAppId;
    @Value("${fz-gateway.secret}")
    private String fzSecret;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 转发福州总部开放平台接口
     *
     * @param apiUrl 接口相对地址，不用"/"开头
     * @param params 接口入参，timestamp不用传
     * @return 响应结果
     * @throws Exception
     */
    public String callFzOpenApi(String apiUrl, Map<String, Object> params) throws Exception {
        ServiceInstance agZuulServiceInstance = discoveryClient.getInstances(MicroServices.AgZuul).get(0);
        // URL前缀与 ag-zuul 配置文件中的 zuul.routes.jkzl-server.path 前缀一致。
        String url = agZuulServiceInstance.getUri() + "/jkzl/" + apiUrl;
        String result = OPUtil.callApi(fzAppId, fzSecret, url, params);
        return result;
    }

}