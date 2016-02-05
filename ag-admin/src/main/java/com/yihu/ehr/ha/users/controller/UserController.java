package com.yihu.ehr.ha.users.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.users.service.UserClient;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0 )
@RestController
@Api(value = "user", description = "用户管理接口，用于用户信息管理", tags = {"用户管理接口"})
public class UserController extends BaseRestController {

    @Autowired
    private UserClient userClient;

    @RequestMapping(value = "/user/search" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表",produces = "application/json", notes = "根据查询条件获取用户列表在前端表格展示")
    public Object searchUsers(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "realName", value = "查询条件", defaultValue = "")
            @RequestParam(value = "realName") String realName,
            @ApiParam(name = "organization", value = "查询条件", defaultValue = "")
            @RequestParam(value = "organization") String organization,
            @ApiParam(name = "searchType", value = "类别", defaultValue = "")
            @RequestParam(value = "searchType") String searchType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "页数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {

        return userClient.searchUsers(apiVersion,realName,organization,searchType,page,rows);
    }

    @RequestMapping(value = "/user/{user_id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",produces = "application/json", notes = "根据用户id删除用户")
    public Object deleteUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception{
        return userClient.deleteUser(apiVersion,userId);
    }

    @RequestMapping(value = "/user/activity/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态",produces = "application/json", notes = "根据用户状态改变当前用户状态")
    public Object  activityUser (
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activated", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activated") boolean activated) throws Exception{
        return userClient.activityUser(apiVersion,userId,activated);
    }

    @RequestMapping(value = "/user" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户",produces = "application/json", notes = "重新绑定用户信息")
    public Object updateUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userModel", value = "用户对象", defaultValue = "")
            @RequestParam(value = "userModel") String userModelJson) throws Exception{
        return userClient.updateUser(apiVersion,userModelJson);

    }

    @RequestMapping(value = "/user/resetPass/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码",produces = "application/json", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public Object resetPass(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception{
        return userClient.resetPass(apiVersion,userId);

    }

    @RequestMapping(value = "/user/{user_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",produces = "application/json", notes = "根据用户id获取用户信息")
    public Object getUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        return userClient.getUser(apiVersion,userId);
    }


    @RequestMapping(value = "/user/user_model/{user_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户对象详细信息",produces = "application/json", notes = "包括地址信息等")
    public Object getUserModel(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        return userClient.getUserModel(apiVersion,userId);
    }

    @RequestMapping(value = "/user/unbundling/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "取消关联绑定",produces = "application/json", notes = "取消相关信息绑定")
    public Object unbundling(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) {

        return userClient.unbundling(apiVersion,userId,type);
    }

    @RequestMapping("/user/distributeKey")
    @ApiOperation(value = "重新分配密钥",produces = "application/json", notes = "重新分配密钥")
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录帐号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode) {
        return userClient.distributeKey(apiVersion,loginCode);
    }

    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "/user/login_indetification" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    public Object loginIndetification(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录账号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @RequestParam(value = "psw") String psw) {
        return userClient.loginIndetification(apiVersion,loginCode,psw);
    }

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/user/login_code" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    public Object getUserByLoginCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录账号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode) {

       return userClient.getUserByLoginCode(apiVersion,loginCode);
    }
}
