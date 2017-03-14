package com.yihu.ehr.portal.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.portal.service.PortalAuthClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeshijie on 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/portal/oauth")
@RestController
@Api(value = "portalAuth", description = "portalAuth", tags = {"token获取及验证接口"})
public class PortalAuthController extends BaseController{

    @Autowired
    private PortalAuthClient portalAuthClient;

    @RequestMapping(value = ServiceApi.PortalAuth.AccessToken, method = RequestMethod.POST)
    @ApiOperation(value = "获取token", notes = "获取token")
    Envelop getAccessToken(
            @ApiParam(name = "userName", value = "登录账号", defaultValue = "")
            @RequestParam(value = "userName") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password,
            @ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
            @RequestParam(value = "clientId") String clientId) {
        try {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("grant_type", "password");
            paramMap.put("client_id", clientId);
            paramMap.put("username", userName);
            paramMap.put("password", password);
            String tokenJson = portalAuthClient.accessToken(paramMap);
            if (tokenJson == null) {
                return failed("授权失败!");
            }
            return success(tokenJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.PortalAuth.RefreshToken, method = RequestMethod.POST)
    @ApiOperation(value = "刷新token", notes = "刷新token")
    Envelop refreshToken(
            @ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
            @RequestParam(value = "clientId") String clientId,
            @ApiParam(name = "refreshToken", value = "刷新Token", defaultValue = "")
            @RequestParam(value = "refreshToken") String refreshToken) {
        try {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("client_id", clientId);
            paramMap.put("grant_type", "refresh_token");
            paramMap.put("client_secret", "admin");
            paramMap.put("refresh_token", refreshToken);
            String tokenJson = portalAuthClient.accessToken(paramMap);
            if (tokenJson == null) {
                return failed("刷新失败!");
            }
            return success(tokenJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.PortalAuth.ValidToken, method = RequestMethod.POST)
    @ApiOperation(value = "验证token", notes = "验证token")
    Envelop validToken(
            @ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
            @RequestParam(value = "clientId") String clientId,
            @ApiParam(name = "accessToken", value = "accessToken", defaultValue = "")
            @RequestParam(value = "accessToken") String accessToken) {
        try {
            String tokenJson = portalAuthClient.validToken(clientId, accessToken);
            if (tokenJson == null) {
                return failed("刷新失败!");
            }
            return success(tokenJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

}
