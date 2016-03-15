package com.yihu.ehr.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.model.MUser;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:55
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(protocols = "https", value = "users", description = "用户服务")
public class UsersEndPoint extends BaseController {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private OrganizationClient organizationClient;

    @ApiOperation("获取用户列表")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<MUser> getUsers() {
        List<MUser> MUsers = new ArrayList<>();
        List<com.yihu.ehr.model.user.MUser> mUsers = userClient.getUsers();
        for (com.yihu.ehr.model.user.MUser mUser : mUsers) {
            MUser MUser = convertToModel(mUser, MUser.class);
            MUser.setOrganization(organizationClient.getOrg(mUser.getOrganization()));
            MUsers.add(MUser);
        }

        return MUsers;
    }

    @ApiOperation("获取用户")
    @RequestMapping(value = "/users/{user_name}", method = RequestMethod.GET)
    public MUser getUser(
            @ApiParam("user_name")
            @PathVariable("user_name") String userName) {
        com.yihu.ehr.model.user.MUser mUser = userClient.getUserByUserName(userName);
        MUser MUser = convertToModel(mUser, MUser.class);

        if (mUser.getOrganization() != null) {
            MUser.setOrganization(organizationClient.getOrg(mUser.getOrganization()));
        }

        return MUser;
    }

    @ApiOperation("获取用户公钥")
    @RequestMapping(value = "/users/{user_name}/key", method = RequestMethod.GET)
    public MKey getPublicKey(@ApiParam("user_name")
                             @PathVariable("user_name")
                             String userName) {
        MKey key = securityClient.getUserKey(userName);
        key.setPrivateKey("");

        return key;
    }
}
