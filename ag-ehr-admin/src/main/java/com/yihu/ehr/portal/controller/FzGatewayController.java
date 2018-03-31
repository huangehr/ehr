package com.yihu.ehr.portal.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.fzgateway.FzGatewayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.30
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
public class FzGatewayController extends BaseController {


    @Value("${service-gateway.url}")
    private String gatewayUrl;
    @Value("${service-gateway.clientId}")
    private String clientId;
    @Value("${service-gateway.clientVersion}")
    private String clientVersion;

    /**
     * 获取健康上饶的用户id
     *
     * @param userId
     * @return
     */
    @GetMapping(ServiceApi.GateWay.FzGateway)
    public String getEhrUserId(@RequestParam(value = "userId",required = true) String userId) throws IOException {
        String api = "UserMgmt.UserAccount.getAccLoginInfo";
        Map<String, Object> apiParamMap = new HashMap<>();
        apiParamMap.put("Userid", userId);//医院类型
        apiParamMap.put("Logintype", clientId);//医院等级
        String resultStr = FzGatewayUtil.httpPost(gatewayUrl, clientId, clientVersion, api, apiParamMap, 1);
        Map<String, String> result = objectMapper.readValue(resultStr, Map.class);
        if (result.get("Code").equals("10000")) {
            String data = result.get("Result");
            List<Map<String, String>> list = objectMapper.readValue(data, new TypeReference<List<Map<String, String>>>() {});
            if (list.size() > 0) {
                Map<String, String> beanMap = list.get(0);
                return beanMap.get("Loginid");
            } else {
                throw new ApiException("获取用户id失败");
            }
        } else {
            throw new ApiException("获取用户id失败");
        }
    }
}
