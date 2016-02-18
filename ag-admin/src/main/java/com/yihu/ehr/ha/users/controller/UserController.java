package com.yihu.ehr.ha.users.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.users.service.UserClient;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0 )
@RestController
@Api(value = "user", description = "用户管理接口，用于用户信息管理", tags = {"用户管理接口"})
public class UserController {

    @Autowired
    private UserClient userClient;

    @RequestMapping(value = "/users" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表",notes = "根据查询条件获取用户列表在前端表格展示")
    public List<MUser> searchUsers(
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
        return userClient.searchUsers(fields,filters,sorts,size,page);
    }

    @RequestMapping(value = "/users/{user_id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",notes = "根据用户id删除用户")
    public Object deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception{
        return userClient.deleteUser(userId);
    }


    @RequestMapping(value = "/users" , method = RequestMethod.POST)
    @ApiOperation(value = "创建用户",notes = "重新绑定用户信息")
    public Object createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) throws Exception{

        return userClient.createUser(userJsonData);

    }


    @RequestMapping(value = "/users" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户",notes = "重新绑定用户信息")
    public Object updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) throws Exception{

        return userClient.updateUser(userJsonData);
    }


    @RequestMapping(value = "users/{user_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息",notes = "包括地址信息等")
    public MUser getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        return userClient.getUser(userId);
    }


    @RequestMapping(value = "/users/{user_id}/{activity}" , method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态",notes = "根据用户状态改变当前用户状态")
    public boolean  activityUser (
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @PathVariable(value = "activity") boolean activity) throws Exception{
        return userClient.activityUser(userId,activity);
    }


    @RequestMapping(value = "users/password/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码",notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public Object resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception{

        return userClient.resetPass(userId);
    }


    @RequestMapping(value = "/users/binding/{user_id}/{type}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关联绑定",notes = "取消相关信息绑定")
    public Object unBinding (
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @PathVariable(value = "type") String type) {

        return userClient.unBinding(userId,type);
    }

    @RequestMapping(value = "/users/users/key/{login_code}", method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥",notes = "重新分配密钥")
    public Object distributeKey(
            @ApiParam(name = "login_code", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {
        return userClient.distributeKey(loginCode);
    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "/users/verification/{login_code}/{psw}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户",notes = "根据登陆用户名及密码验证用户")
    public MUser loginVerification(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @PathVariable(value = "psw") String psw) {
        return userClient.loginVerification(loginCode,psw);
    }

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/users/{login_code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户",notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByLoginCode(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {
        return userClient.getUserByLoginCode(loginCode);
    }
}
