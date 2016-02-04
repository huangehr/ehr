package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.feign.ConventionalDictClient;
import com.yihu.ehr.user.feign.OrgClient;
import com.yihu.ehr.user.feign.SecurityClient;
import com.yihu.ehr.user.service.User;
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.user.service.UserModel;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/user")
@Api(protocols = "https", value = "user", description = "用户管理接口", tags = {"用户", "登录帐号", "密码"})
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private OrgClient organizationClient;

    @Autowired
    SecurityClient securityClient;

    @RequestMapping(value = "/search" , method = RequestMethod.GET)
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

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("realName", realName);
        conditionMap.put("organization", organization);
        conditionMap.put("type", searchType);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);

        List<User> userList = userManager.searchUser(apiVersion,conditionMap);
        List<MUser> userModelList = new ArrayList<MUser>();
        for (User user: userList){
            MUser userModel = convertToModel(user,MUser.class);
            if(user.getUserType()!=null){
                userModel.setUserType(conventionalDictClient.getUserType(apiVersion,user.getUserType()));
            }
            if(user.getMartialStatus()!=null){
                userModel.setMartialStatus(conventionalDictClient.getMartialStatus(apiVersion,user.getMartialStatus()));
            }
            if(user.getGender()!=null){
                userModel.setGender(conventionalDictClient.getGender(apiVersion,user.getGender()));
            }
            if(user.getOrganization()!=null){
                userModel.setOrganization(organizationClient.getOrgByCode(apiVersion,user.getOrganization()));
            }
            userModelList.add(userModel);
        }
        Integer totalCount = userManager.searchUserInt(apiVersion,conditionMap);
        return getResult(userModelList,totalCount,page,rows);
    }

    @RequestMapping(value = "" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",produces = "application/json", notes = "根据用户id删除用户")
    public Object deleteUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{
        userManager.deleteUser(userId);
        return true;
    }


    @RequestMapping(value = "" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户",produces = "application/json", notes = "重新绑定用户信息")
    public Object updateUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String userModelJsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        UserModel userModel = objectMapper.readValue(userModelJsonData, UserModel.class);
        userManager.updateUser(apiVersion,userModel);
        return true;

    }


    @RequestMapping(value = "" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息",produces = "application/json", notes = "包括地址信息等")
    public MUser getUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @RequestParam(value = "user_id") String userId) {
        User user = userManager.getUser(userId);
        MUser userModel = convertToModel(user,MUser.class);
        if(user.getUserType()!=null){
            userModel.setUserType(conventionalDictClient.getUserType(apiVersion,user.getUserType()));
        }
        if(user.getMartialStatus()!=null){
            userModel.setMartialStatus(conventionalDictClient.getMartialStatus(apiVersion,user.getMartialStatus()));
        }
        if(user.getGender()!=null){
            userModel.setGender(conventionalDictClient.getGender(apiVersion,user.getGender()));
        }
        if(user.getOrganization()!=null){
            userModel.setOrganization(organizationClient.getOrgByCode(apiVersion,user.getOrganization()));
        }
        return userModel;
    }


    @RequestMapping(value = "/{user_id}/{activity}" , method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态",produces = "application/json", notes = "根据用户状态改变当前用户状态")
    public boolean  activityUser (
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @PathVariable(value = "activity") boolean activity) throws Exception{
        userManager.activityUser(userId, activity);
        return true;
    }


    @RequestMapping(value = "/reset_pass/{user_id}" , method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码",produces = "application/json", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public Object resetPass(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception{
        userManager.resetPass(userId);
        return true;

    }


    @RequestMapping(value = "/un_binding/{user_id}/{type}" , method = RequestMethod.PUT)
    @ApiOperation(value = "取消关联绑定",produces = "application/json", notes = "取消相关信息绑定")
    public Object unBinding (
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @PathVariable(value = "type") String type) {

        User user = userManager.getUser(userId);
        if (type.equals("tel")) {
            //tel尚未数据库映射
            user.setTelephone("");
        } else {
            user.setEmail("");
        }
        userManager.saveUser(user);
        return true;
    }

    @RequestMapping("/distributeKey/{login_code}")
    @ApiOperation(value = "重新分配密钥",produces = "application/json", notes = "重新分配密钥")
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "login_code", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {
        MUserSecurity userSecurity = securityClient.getUserSecurityByLoginCode(apiVersion,loginCode);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity == null) {
            User userInfo = userManager.getUserByLoginCode(loginCode);
            String userId = userInfo.getId();
            userSecurity = securityClient.createSecurityByUserId(apiVersion,userId);
        }else{
            //result.setErrorMsg("公钥信息已存在。");
            //这里删除原有的公私钥重新分配
            //1-1根据用户登陆名获取用户信息。
            User userInfo = userManager.getUserByLoginCode(loginCode);
            String userId = userInfo.getId();
            String userKeyId = securityClient.getUserKeyByUserId(apiVersion,userId);
            securityClient.deleteSecurity(apiVersion,userSecurity.getId());
            securityClient.deleteUserKey(apiVersion,userKeyId);
            userSecurity = securityClient.createSecurityByUserId(apiVersion,userId);

        }
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
        return keyMap;
    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "login_verification/{login_code}/{psw}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    public User loginVerification(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @PathVariable(value = "psw") String psw) {
        return  userManager.loginVerification(loginCode,psw);
    }

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/{login_code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByLoginCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {
        User user = userManager.getUserByLoginCode(loginCode);
        return convertToModel(user,MUser.class);
    }
}
