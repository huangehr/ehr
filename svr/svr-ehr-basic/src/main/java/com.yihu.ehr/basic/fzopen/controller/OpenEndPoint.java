package com.yihu.ehr.basic.fzopen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.fzopen.utils.OPPlaintTextUtil;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 转发福州总部开放平台接口
 *
 * @author 张进军
 * @date 2018/4/12 18:42
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "转发福州总部开放平台接口", tags = {"转发福州总部开放平台接口"})
public class OpenEndPoint extends EnvelopRestEndPoint {

    @Value("${fz-gateway.clientId}")
    private String fzAppId;
    @Value("${fz-gateway.secret}")
    private String fzSecret;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiscoveryClient discoveryClient;

    @ApiOperation("转发福州总部开放平台接口")
    @RequestMapping(value = ServiceApi.Fz.Open, method = RequestMethod.POST)
    public Envelop fzHttpPost(
            @ApiParam(name = "apiUrl", value = "相对接口路径，不用\"/\"开头", required = true)
            @RequestParam(value = "apiUrl") String apiUrl,
            @ApiParam(name = "paramsJson", value = "参数JSON字符串", required = true)
            @RequestParam(value = "paramsJson") String paramsJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, String> params = objectMapper.readValue(paramsJson, Map.class);
            ServiceInstance serviceInstance = discoveryClient.getInstances(MicroServices.AgZuul).get(0);
            if (serviceInstance != null) {
                // URL前缀与 ag-zuul 配置文件中的 zuul.routes.jkzl-server.path 前缀一致。
                String url = serviceInstance.getUri() + "/jkzl/" + apiUrl;
                String result = OPPlaintTextUtil.callApi(fzAppId, fzSecret, url, params);
                envelop.setObj(result);
                envelop.setSuccessFlg(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
