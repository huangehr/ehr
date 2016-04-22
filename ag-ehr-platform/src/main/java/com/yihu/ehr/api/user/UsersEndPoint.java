package com.yihu.ehr.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.model.OrgModel;
import com.yihu.ehr.api.model.UserModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Api(value = "users", description = "用户服务")
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
    public List<UserModel> getUsers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        List<UserModel> UserModels = new ArrayList<>();
        List<com.yihu.ehr.model.user.MUser> mUsers = userClient.getUsers(fields, filters, sorts, size, page);
        for (com.yihu.ehr.model.user.MUser mUser : mUsers) {
            UserModel UserModel = convertToModel(mUser, UserModel.class);
            if (mUser.getOrganization() != null) {
                MOrganization mOrganization = organizationClient.getOrg(mUser.getOrganization());
                UserModel.setOrganization(convertToModel(mOrganization, OrgModel.class));
            }

            UserModels.add(UserModel);
        }

        return UserModels;
    }

    @ApiOperation("获取用户")
    @RequestMapping(value = "/users/{user_name}", method = RequestMethod.GET)
    public UserModel getUser(
            @ApiParam("user_name")
            @PathVariable("user_name") String userName) {
        MUser mUser = userClient.getUserByUserName(userName);
        UserModel UserModel = convertToModel(mUser, UserModel.class);

        if (mUser.getOrganization() != null) {
            MOrganization mOrganization = organizationClient.getOrg(mUser.getOrganization());
            UserModel.setOrganization(convertToModel(mOrganization, OrgModel.class));
        }

        return UserModel;
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
