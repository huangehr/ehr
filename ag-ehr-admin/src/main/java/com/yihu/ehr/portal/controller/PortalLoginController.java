package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.user.UsersModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.users.service.UserClient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by progr1mmer on 2018/1/11.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
public class PortalLoginController extends BaseController {

    @Autowired
    private UserClient userClient;

    @RequestMapping(value = ServiceApi.Portal.Login, method = RequestMethod.GET)
    @ApiOperation(value = "用户名密码验证", notes = "用户名密码验证")
    Result login(
            @ApiParam(name = "userName", value = "登录账号", defaultValue = "")
            @RequestParam(value = "userName") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) {
        try {
            UsersModel usersModel = new UsersModel();
            MUser user = userClient.getUserByNameAndPassword(userName, password);
            if (user == null) {
                return Result.error("登录失败，用户名或密码不正确!");
            }else {
                if(!user.getActivated()){
                    return Result.error("该账户已被锁定，请联系系统管理员重新生效!");
                }
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
