package com.yihu.ehr.ha.users.service;

import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient("svr-user")
public interface UserClient {

    @RequestMapping(value = "/rest/{api_version}/user/search" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表",produces = "application/json", notes = "根据查询条件获取用户列表在前端表格展示")
    Object searchUsers(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "realName", value = "查询条件", defaultValue = "")
            @RequestParam(value = "realName") String realName,
            @ApiParam(name = "organization", value = "查询条件", defaultValue = "")
            @RequestParam(value = "organization") String organization,
            @ApiParam(name = "searchType", value = "类别", defaultValue = "")
            @RequestParam(value = "searchType") String searchType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "页数", defaultValue = "")
            @RequestParam(value = "rows") int rows) ;

    @RequestMapping(value = "/rest/{api_version}/user/" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",produces = "application/json", notes = "根据用户id删除用户")
    Object deleteUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) ;

    @RequestMapping(value = "/rest/{api_version}/user/activity" , method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态",produces = "application/json", notes = "根据用户状态改变当前用户状态")
    Object  activityUser (
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "activated", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activated") boolean activated);

    @RequestMapping(value = "/rest/{api_version}/user/" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户",produces = "application/json", notes = "重新绑定用户信息")
    Object updateUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userModel", value = "用户对象", defaultValue = "")
            @RequestParam(value = "userModel") String userModelJson);

    @RequestMapping(value = "/rest/{api_version}/user/resetPass" , method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码",produces = "application/json", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    Object resetPass(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) ;

    @RequestMapping(value = "/rest/{api_version}/user/" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",produces = "application/json", notes = "根据用户id获取用户信息")
    Object getUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId) ;


    @RequestMapping(value = "/rest/{api_version}/user/user_model" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户对象详细信息",produces = "application/json", notes = "包括地址信息等")
    Object getUserModel(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId) ;

    @RequestMapping(value = "/rest/{api_version}/user/unbundling" , method = RequestMethod.PUT)
    @ApiOperation(value = "取消关联绑定",produces = "application/json", notes = "取消相关信息绑定")
    Object unbundling(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) ;

    @RequestMapping("/rest/{api_version}/user/distributeKey")
    @ApiOperation(value = "重新分配密钥",produces = "application/json", notes = "重新分配密钥")
    Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录帐号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode) ;


    @RequestMapping(value = "/rest/{api_version}/user/login_indetification" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    Object loginIndetification(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录账号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @RequestParam(value = "psw") String psw);

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/rest/{api_version}/user/login_code" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    MUser getUserByLoginCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录账号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode) ;
}
