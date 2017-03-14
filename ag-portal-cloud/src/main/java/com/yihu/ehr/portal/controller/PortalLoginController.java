package com.yihu.ehr.portal.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.portal.service.PortalAuthClient;
import com.yihu.ehr.portal.service.PortalLoginClient;
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

/**
 * Created by yeshijie on 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/portal")
@RestController
@Api(value = "portalLogin", description = "portalLogin", tags = {"单点登录验证"})
public class PortalLoginController extends BaseController{

    @Autowired
    private PortalLoginClient portalLoginClient;

    @RequestMapping(value = ServiceApi.PortalLogin.PortalLogin, method = RequestMethod.GET)
    @ApiOperation(value = "单点登录验证", notes = "单点登录验证")
    Envelop getUserByNameAndPassword(
            @ApiParam(name = "userName", value = "登录账号", defaultValue = "")
            @RequestParam(value = "userName") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) {
        try {
            Envelop result = new Envelop();
            MUser user = portalLoginClient.getUserByNameAndPassword(userName, password);
            if (user == null) {
                return failed("登录失败，用户名密码不正确!");
            }

            result.setObj(user);
            result.setSuccessFlg(true);
            result.setErrorMsg("登录成功!");
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

}
