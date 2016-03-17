package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.feign.SecurityClient;
import com.yihu.ehr.user.service.User;
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encode.HashUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "users", description = "用户管理接口", tags = {"用户", "登录帐号", "密码"})
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private SecurityClient securityClient;

    @RequestMapping(value = RestApi.Users.Users, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户列表在前端表格展示")
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
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<User> userList = userManager.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, userManager.getCount(filters), page, size);
        return (List<MUser>) convertToModels(userList, new ArrayList<MUser>(userList.size()), MUser.class, fields);
    }

    @RequestMapping(value = RestApi.Users.Users, method = RequestMethod.POST)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    public MUser createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) throws Exception {
        User user = new ObjectMapper().readValue(userJsonData, User.class);
        user.setId(getObjectId(BizObject.User));
        user.setCreateDate(new Date());
        user.setPassword(HashUtil.hashStr(user.getPassword()));
        user.setActivated(true);
        userManager.saveUser(user);
        return convertToModel(user, MUser.class, null);
    }

    @RequestMapping(value = RestApi.Users.Users, method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public MUser updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(userJsonData, User.class);
        userManager.saveUser(user);
        return convertToModel(user, MUser.class, null);
    }

    @RequestMapping(value = RestApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取用户信息")
    public MUser getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        User user = userManager.getUser(userId);
        MUser userModel = convertToModel(user, MUser.class);
        return userModel;
    }

    @RequestMapping(value = RestApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据id删除用户")
    public boolean deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userManager.deleteUser(userId);
        return true;
    }

    @RequestMapping(value = RestApi.Users.UserAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据id更新用户")
    public boolean activityUser(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activity") boolean activity) throws Exception {
        userManager.activityUser(userId, activity);
        return true;
    }

    /**
     * 根据用户名获取user
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = RestApi.Users.User, method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByLoginCode(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @PathVariable(value = "user_name") String userName) {
        User user = userManager.getUserByLoginCode(userName);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = RestApi.Users.UserAdminPassword, method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userManager.resetPass(userId);

        return true;
    }

    @RequestMapping(value = RestApi.Users.UserAdminKey, method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "user_id", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        MKey userSecurity = securityClient.getUserSecurityByUserId(userId);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity == null) {
            userSecurity = securityClient.createSecurityByUserId(userId);
        } else {
            // 删除原有的公私钥重新分配
            String userKeyId = securityClient.getUserKeyByUserId(userId);
            securityClient.deleteSecurity(userSecurity.getId());
            securityClient.deleteUserKey(userKeyId);
            userSecurity = securityClient.createSecurityByUserId(userId);

        }
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd"));
        return keyMap;
    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param userName
     * @param password
     */
    @RequestMapping(value = RestApi.Users.UserPassword, method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByNameAndPassword(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @PathVariable(value = "user_name") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @PathVariable(value = "password") String password) {
        User user = userManager.loginVerification(userName, password);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = RestApi.Users.UserExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户名是否存在")
    public boolean isUserNameExists(
            @ApiParam(name = "login_code", value = "login_code", defaultValue = "")
            @PathVariable(value = "login_code") String userName) {
        return userManager.getUserByLoginCode(userName) != null;
    }

    @RequestMapping(value = RestApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户身份证号是否存在")
    public boolean isIdCardExists(
            @ApiParam(name = "id_card", value = "id_card", defaultValue = "")
            @PathVariable(value = "id_card") String idCard) {
        return userManager.getUserByIdCardNo(idCard) != null;
    }

    @RequestMapping(value = RestApi.Users.UserAdminContact, method = RequestMethod.DELETE)
    @ApiOperation(value = "用户联系方式解绑", notes = "将用户电话或邮件地址设置为空")
    public boolean delteContact(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) {
        User user = userManager.getUser(userId);
        if (type.equals("tel")) {
            user.setTelephone("");
        } else {
            user.setEmail("");
        }

        userManager.saveUser(user);
        return true;
    }
}
