package com.yihu.ehr.ha.users.service;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
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
@FeignClient(MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = RestApi.Users.Users, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户列表在前端表格展示")
    ResponseEntity<List<MUser>> searchUsers(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = RestApi.Users.Users, method = RequestMethod.POST)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    MUser createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData);

    @RequestMapping(value = RestApi.Users.Users, method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    MUser updateUser(@RequestParam(value = "user_json_data") String userJsonData);

    @RequestMapping(value = RestApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据用户id删除用户")
    boolean deleteUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = RestApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "包括地址信息等")
    MUser getUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = RestApi.Users.UserAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据用户状态改变当前用户状态")
    boolean activityUser(
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "activity") boolean activity);

    @RequestMapping(value = RestApi.Users.UserAdminPassword, method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = RestApi.Users.UserAdminContact, method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关联绑定", notes = "取消相关信息绑定")
    boolean unBinding(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type);

    @RequestMapping(value = RestApi.Users.UserAdminKey, method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    Map<String, String> distributeKey(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = RestApi.Users.UserPassword, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户名及密码获取用户", notes = "根据用户名及密码获取用户")
    MUser getUserByNameAndPassword(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @PathVariable(value = "user_name") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @PathVariable(value = "password") String password);

    @RequestMapping(value = RestApi.Users.User, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户名获取用户", notes = "根据用户名获取用户")
    MUser getUserByUserName(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value = RestApi.Users.UserExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断账户是否存在")
    boolean isUserNameExists(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value = RestApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    boolean isIdCardExists(@PathVariable(value = "id_card") String idCard);
}
