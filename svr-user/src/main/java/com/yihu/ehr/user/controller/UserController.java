package com.yihu.ehr.user.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.feignClient.org.OrgClient;
import com.yihu.ehr.user.feignClient.security.SecurityClient;
import com.yihu.ehr.user.service.User;
import com.yihu.ehr.user.service.UserDetailModel;
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.user.service.UserModel;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/user")
@Api(protocols = "https", value = "user", description = "用户管理接口", tags = {"用户", "登录帐号", "密码"})
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private OrgClient organizationClient;

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

        List<UserDetailModel> detailModelList = userManager.searchUserDetailModel(apiVersion,conditionMap);

        Integer totalCount = userManager.searchUserInt(apiVersion,conditionMap);
        return getResult(detailModelList,totalCount,page,rows);
    }

    @RequestMapping(value = "/" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",produces = "application/json", notes = "根据用户id删除用户")
    public Object deleteUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{
        userManager.deleteUser(userId);
        return "success";
    }

    @RequestMapping(value = "/activity" , method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态",produces = "application/json", notes = "根据用户状态改变当前用户状态")
    public Object  activityUser (
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "activated", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activated") boolean activated) throws Exception{
        userManager.activityUser(userId, activated);
        return "success";
    }

    @RequestMapping(value = "/" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户",produces = "application/json", notes = "重新绑定用户信息")
    public Object updateUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userModel", value = "用户对象", defaultValue = "")
            @RequestParam(value = "userModel") UserModel userModel) throws Exception{
        userManager.updateUser(apiVersion,userModel);
        return "success";

    }

    @RequestMapping(value = "/resetPass" , method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码",produces = "application/json", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public Object resetPass(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{
        userManager.resetPass(userId);
        return "success";

    }

    @RequestMapping(value = "/" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",produces = "application/json", notes = "根据用户id获取用户信息")
    public Object getUser(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        User user = userManager.getUser(userId);
        return user;
    }


    @RequestMapping(value = "/user_model" , method = RequestMethod.GET)
    @ApiOperation(value = "获取用户对象详细信息",produces = "application/json", notes = "包括地址信息等")
    public Object getUserModel(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        Map<String,Object> map = new HashMap<>();
        User user = userManager.getUser(userId);
        UserModel userModel = userManager.getUser(apiVersion,user);
        MOrganization org = null;
        if(userModel.getOrgCode()!=null){
            org = organizationClient.getOrg(apiVersion,userModel.getOrgCode());
        }
        MAddress addressModel = new MAddress();
        if(org!=null){
            String locarion = org.getLocation();
            if(!"".equals(locarion)){
                addressModel = userManager.getAddressById(apiVersion,locarion);
                map.put("orgLocation",addressModel);
            }
        }
        map.put("user",userModel);
        return map;
    }

    @RequestMapping(value = "/unbundling" , method = RequestMethod.PUT)
    @ApiOperation(value = "取消关联绑定",produces = "application/json", notes = "取消相关信息绑定")
    public Object unbundling(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) {

        User user = userManager.getUser(userId);
        if (type.equals("tel")) {
            //tel尚未数据库映射
            user.setTelephone("");
        } else {
            user.setEmail("");
        }
        userManager.updateUser(user);
        return "success";
    }

    @RequestMapping("/distributeKey")
    @ApiOperation(value = "重新分配密钥",produces = "application/json", notes = "重新分配密钥")
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录帐号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode) {
        MUserSecurity userSecurity = securityClient.getUserSecurityByOrgName(apiVersion,loginCode);
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
            securityClient.deleteUserKey(userKeyId);
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
     * 检查用户是否存在
     * @param type
     * @param searchNm
     * @return
     */
//    @RequestMapping(value = "/user/isExit")
//    @ApiOperation(value = "验证用户是否存在",produces = "application/json", notes = "根据用户id查询用户，查看此用户是否存在")
//    public Object searchUser(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "type", value = "机构类型", defaultValue = "")
//            @RequestParam(value = "type") String type,
//            @ApiParam(name = "searchNm", value = "查询条件", defaultValue = "")
//            @RequestParam(value = "searchNm") String searchNm){
//
//        List<User> list = userManager.searchUser(type,searchNm);
//        if(list.size()>0){
//            return false;
//        }else{
//            return true;
//        }
//    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "/login_indetification" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    public User loginIndetification(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录账号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @RequestParam(value = "psw") String psw) {
        return  userManager.loginIndetification(loginCode,psw);
    }

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/login_code" , method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户",produces = "application/json", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByLoginCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "登录账号", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode) {
        User user = userManager.getUserByLoginCode(loginCode);
        MUser userModel = new MUser();
        BeanUtils.copyProperties(user,userModel);
        return userModel;
    }
}
