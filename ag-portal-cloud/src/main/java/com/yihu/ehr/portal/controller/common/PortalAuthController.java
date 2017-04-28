package com.yihu.ehr.portal.controller.common;

import com.yihu.ehr.agModel.user.UsersModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.portal.service.function.PortalLoginClient;
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
@Api(value = "portalAuth", description = "portalAuth", tags = {"安全管理-Token获取及验证接口"})
public class PortalAuthController extends BaseController{


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


}
