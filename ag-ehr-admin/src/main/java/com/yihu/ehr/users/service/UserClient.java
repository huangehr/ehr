package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient(name = MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户列表在前端表格展示")
    ResponseEntity<List<MUser>> searchUsers(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    MUser createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestBody String userJsonData);

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    MUser updateUser(@RequestBody String userJsonData);

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据用户id删除用户")
    boolean deleteUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "包括地址信息等")
    MUser getUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据用户状态改变当前用户状态")
    boolean activityUser(
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "activity") boolean activity);

    @RequestMapping(value = ServiceApi.Users.UserAdminPassword, method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ServiceApi.Users.UserAdminContact, method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关联绑定", notes = "取消相关信息绑定")
    boolean unBinding(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type);

    @RequestMapping(value = ServiceApi.Users.UserAdminKey, method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    Map<String, String> distributeKey(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ServiceApi.Users.UserVerification, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户名及密码获取用户", notes = "根据用户名及密码获取用户")
    MUser getUserByNameAndPassword(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @RequestParam(value = "user_name") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password);

    @RequestMapping(value = ServiceApi.Users.User, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户名获取用户", notes = "根据用户名获取用户")
    MUser getUserByUserName(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value = ServiceApi.Users.UserExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断账户是否存在")
    boolean isUserNameExists(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value = ServiceApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    boolean isIdCardExists(@RequestParam(value = "id_card_no") String idCardNo);

    @RequestMapping(value = ServiceApi.Users.UserEmailNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户邮件是否存在")
    boolean isEmailExists(@RequestParam(value = "email") String email);

    @RequestMapping(value = ServiceApi.Users.UserTelephoneNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断电话号码是否存在")
    boolean isTelephoneExists(@RequestParam(value = "telephone") String telephone);

    @RequestMapping(value = ServiceApi.Users.UserAdminPasswordReset, method = RequestMethod.PUT)
    @ApiOperation(value = "修改密码")
    boolean changePassWord(
            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password);

    @RequestMapping(value = "/user/picture", method = RequestMethod.POST)
    @ApiOperation(value = "头像上传")
    String uploadPicture(
            @ApiParam(name = "jsonData", value = "头像信息", defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = "/user/picture", method = RequestMethod.GET)
    @ApiOperation(value = "头像下载")
    String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files", method = RequestMethod.POST)
    @ApiOperation(value = "file upload test")
    public String pictureUpload(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data") String jsonData);

    @RequestMapping(value = ServiceApi.Users.UserPhoneExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    List idExistence(
            @RequestBody String phones);
}
