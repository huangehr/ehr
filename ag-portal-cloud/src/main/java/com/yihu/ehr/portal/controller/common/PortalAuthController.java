package com.yihu.ehr.portal.controller.common;

import com.yihu.ehr.agModel.user.UsersModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.portal.service.common.PortalAuthClient;
import com.yihu.ehr.portal.service.PortalLoginClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private PortalLoginClient portalLoginClient;

    @RequestMapping(value = ServiceApi.PortalLogin.PortalLogin, method = RequestMethod.GET)
    @ApiOperation(value = "用户名密码验证", notes = "用户名密码验证")
    Result getUserByNameAndPassword(
            @ApiParam(name = "userName", value = "登录账号", defaultValue = "")
            @RequestParam(value = "userName") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) {
        try {
            UsersModel usersModel = new UsersModel();
            MUser user = portalLoginClient.getUserByNameAndPassword(userName, password);

            if (user == null) {
                return Result.error("登录失败，用户名或密码不正确!");
            }

            //允许开放的用户信息
            usersModel.setId(user.getId());
            usersModel.setRealName(user.getRealName());
            usersModel.setEmail(user.getEmail());
            usersModel.setOrganizationCode(user.getOrganization());
            usersModel.setTelephone(user.getTelephone());
            usersModel.setLoginCode(user.getLoginCode());
            usersModel.setUserTypeName(user.getUserType());
            usersModel.setActivated(user.getActivated());

            ObjectResult re = new ObjectResult(true,"登录成功!");
            re.setData(usersModel);
            return re;

        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 密码模式，生成token信息
     * @param userName
     * @param password
     * @param clientId
     * @return
     */
    @RequestMapping(value = ServiceApi.PortalAuth.AccessToken, method = RequestMethod.POST)
    @ApiOperation(value = "获取token", notes = "获取token")
    Result getAccessToken(
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
            if (StringUtils.isEmpty(tokenJson)) {
                return Result.error("授权失败!");
            }
            ObjectResult re = new ObjectResult(true,"获取Token成功！");
            re.setData(tokenJson);
            return re;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.PortalAuth.RefreshToken, method = RequestMethod.POST)
    @ApiOperation(value = "刷新token", notes = "刷新token")
    Result refreshToken(
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
            if (StringUtils.isEmpty(tokenJson)) {
                return Result.error("刷新token失败!");
            }
            ObjectResult re = new ObjectResult(true,"刷新Token成功！");
            re.setData(tokenJson);
            return re;

        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.PortalAuth.ValidToken, method = RequestMethod.POST)
    @ApiOperation(value = "验证token", notes = "验证token")
    Result validToken(
            @ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
            @RequestParam(value = "clientId") String clientId,
            @ApiParam(name = "accessToken", value = "accessToken", defaultValue = "")
            @RequestParam(value = "accessToken") String accessToken) {
        try {
            String validRes = portalAuthClient.validToken(clientId, accessToken);
            if (StringUtils.isEmpty(validRes)) {
                return Result.error("验证失败!");
            }

            return objectMapper.readValue(validRes,Result.class);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }
}
