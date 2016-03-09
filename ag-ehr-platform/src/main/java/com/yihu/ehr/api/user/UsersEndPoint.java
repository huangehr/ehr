package com.yihu.ehr.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.model.OrganizationModel;
import com.yihu.ehr.api.model.UserModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.user.MUser;
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
 * 用户认证网关。
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
    private OrganizationClient organizationClient;

    @ApiOperation("获取用户列表")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserModel> getUsers() {
        List<UserModel> userModels = new ArrayList<>();
        List<MUser> mUsers = userClient.getUsers();
        for (MUser mUser : mUsers) {
            UserModel userModel = convertToModel(mUser, UserModel.class);
            userModel.setOrganization(organizationClient.getOrg(mUser.getOrganization()));
            userModels.add(userModel);
        }

        return userModels;
    }

    @ApiOperation("获取用户")
    @RequestMapping(value = "/users/{user_name}", method = RequestMethod.GET)
    public UserModel getUser(
            @ApiParam("user_name")
            @PathVariable("user_name") String userName) {
        MUser mUser = userClient.getUserByUserName(userName);
        UserModel userModel = convertToModel(mUser, UserModel.class);

        //包括机构信息
        userModel.setOrganization(organizationClient.getOrg(mUser.getOrganization()));
        return userModel;
    }

    @ApiOperation(value = "获取你自己的信息", notes = "/users/{user_name}的快捷方式")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getAuthorizedUser(HttpServletRequest request) {
        return "";
    }

    @ApiOperation(value = "更新你自己的信息")
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public String updateAuthorizedUser(@ApiParam("user")
                                       @RequestParam("user")
                                       String userJson,
                                       HttpServletRequest request) {
        return "";
    }

    //------ 用户RSA公钥 ------
    @ApiOperation("获取用户公钥列表")
    @RequestMapping(value = "/users/{user_name}/keys", method = RequestMethod.GET)
    public List<String> getPublicKeys(@ApiParam("user_name")
                                      @PathVariable("user_name")
                                      String userName) {
        return null;
    }

    @ApiOperation("获取你的公钥列表")
    @RequestMapping(value = "/user/keys", method = RequestMethod.GET)
    public List<String> getPublicKeys(HttpServletRequest request) {
        return null;
    }

    @ApiOperation("为你创建公钥")
    @RequestMapping(value = "/user/keys", method = RequestMethod.POST)
    public List<String> createPublicKey(String userName,
                                        @ApiParam("key_id")
                                        @RequestParam("key_id")
                                        String keyId) {
        return null;
    }

    @ApiOperation("获取你的公钥")
    @RequestMapping(value = "/user/keys/{key_id}", method = RequestMethod.GET)
    public List<String> getPublicKey(@ApiParam("key_id")
                                     @PathVariable("key_id")
                                     String keyId) {
        return null;
    }

    @ApiOperation("删除你的公钥")
    @RequestMapping(value = "/user/keys/{key_id}", method = RequestMethod.DELETE)
    public List<String> deletePublicKey(@ApiParam("key_id")
                                        @PathVariable("key_id")
                                        String keyId) {
        return null;
    }
}
