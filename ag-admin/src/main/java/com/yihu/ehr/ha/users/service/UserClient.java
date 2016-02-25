package com.yihu.ehr.ha.users.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient("svr-user")
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = "/users" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表",notes = "根据查询条件获取用户列表在前端表格展示")
    List<MUser> searchUsers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) ;

    @RequestMapping(value = "/users/{user_id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",notes = "根据用户id删除用户")
    boolean deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) ;


    @RequestMapping(value = "/users" , method = RequestMethod.POST)
    @ApiOperation(value = "创建用户",notes = "重新绑定用户信息")
    MUser createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData);


    @RequestMapping(value = "/users" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户",notes = "重新绑定用户信息")
    MUser updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData);


    @RequestMapping(value = "/users/{user_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息",notes = "包括地址信息等")
    MUser getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) ;


    @RequestMapping(value = "/users/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态",notes = "根据用户状态改变当前用户状态")
    boolean  activityUser (
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activity") boolean activity) ;


    @RequestMapping(value = "/users/password/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码",notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) ;


    @RequestMapping(value = "/users/binding/{user_id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关联绑定",notes = "取消相关信息绑定")
    boolean unBinding (
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type);

    @RequestMapping(value = "/users/key/{user_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥",notes = "重新分配密钥")
    Map<String, String> distributeKey(
            @ApiParam(name = "user_id", value = "用户ID", defaultValue = "")
            @PathVariable(value = "user_id") String userId);


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "/users/verification/{login_code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户",notes = "根据登陆用户名及密码验证用户")
    MUser loginVerification(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @RequestParam(value = "psw") String psw);

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/users/login/{login_code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户",notes = "根据登陆用户名及密码验证用户")
    MUser getUserByLoginCode(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode);
}
