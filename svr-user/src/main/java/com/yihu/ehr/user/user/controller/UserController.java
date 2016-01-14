package com.yihu.ehr.user.user.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.model.address.AddressModel;
import com.yihu.ehr.model.org.OrganizationModel;
import com.yihu.ehr.model.security.UserSecurityModel;
import com.yihu.ehr.user.user.service.*;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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
@RequestMapping("/user")
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private SecurityClient securityClient;



    @RequestMapping(value = "initial" , method = RequestMethod.GET)
    public String userInitial(Model model) {
        model.addAttribute("contentPage","user/user");
        return "pageView";
    }

    @RequestMapping(value = "addUserInfoDialog" , method = RequestMethod.GET)
    public String addUser(Model model){
        model.addAttribute("contentPage","user/addUserInfoDialog");
        return "generalView";
    }

    @RequestMapping(value = "users" , method = RequestMethod.GET)
    public Object searchUsers(
            @ApiParam(name = "searchNm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "searchType", value = "类别", defaultValue = "")
            @RequestParam(value = "searchType") String searchType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "页数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("realName", searchNm);
        conditionMap.put("organization", searchNm);
        conditionMap.put("type", searchType);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        BaseController baseController = new BaseController();

        List<UserDetailModel> detailModelList = userManager.searchUserDetailModel(conditionMap);

        Integer totalCount = userManager.searchUserInt(conditionMap);
        Result result = baseController.getResult(detailModelList, totalCount, page, rows);

        return result;
    }

    @RequestMapping(value = "user" , method = RequestMethod.DELETE)
    public Object deleteUser(
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{
        userManager.deleteUser(userId);
        return "success";
    }

    @RequestMapping(value = "activity" , method = RequestMethod.PUT)
    public Object  activityUser (
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "activated", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activated") boolean activated) throws Exception{

        userManager.activityUser(userId, activated);
        return "success";
    }

    @RequestMapping(value = "user" , method = RequestMethod.PUT)
    public Object updateUser(
            @ApiParam(name = "userModel", value = "用户对象", defaultValue = "")
            @RequestParam(value = "userModel") UserModel userModel) throws Exception{
        userManager.updateUser(userModel);
        return "success";

    }

    @RequestMapping(value = "resetPass" , method = RequestMethod.PUT)
    public Object resetPass(
            @ApiParam(name = "userId", value = "id", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{
        userManager.resetPass(userId);
        return "success";

    }

    @RequestMapping(value = "user" , method = RequestMethod.GET)
    public Object getUser(
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "mode", value = "", defaultValue = "")
            @RequestParam(value = "mode") String mode) {
        Map<String,Object> map = new HashMap<>();
        User user = userManager.getUser(userId);
        UserModel userModel = userManager.getUser(user);
        OrganizationModel org = null;
        if(userModel.getOrgCode()!=null){
            org = userManager.getgetOrg(userModel.getOrgCode());
        }
        AddressModel addressModel = new AddressModel();
        if(org!=null && !"00000".equals(org.getOrgCode())){
            String locarion = org.getLocation();
            if(!"".equals(locarion)){
                addressModel = userManager.getAddressById(locarion);
                map.put("orgLocation",addressModel);
            }
        }
        map.put("user",userModel);
        map.put("mode",mode);
        map.put("contentPage","user/userInfoDialog");
        return map;
    }

    @RequestMapping(value = "unbundling" , method = RequestMethod.PUT)
    public Object unbundling(
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) {

        User user = userManager.getUser(userId);
        if (type.equals("tel")) {
            //tel尚未数据库映射
            user.setEmail("");
        } else {
            user.setEmail("");
        }
        userManager.updateUser(user);
        return "success";
    }

    @RequestMapping("distributeKey")
    @ResponseBody
    public Object distributeKey(String loginCode) {
        try {
            UserSecurityModel userSecurity = securityClient.getUserSecurityByUserName(loginCode);
            Map<String, String> keyMap = new HashMap<>();
            if (userSecurity == null) {
                User userInfo = userManager.getUserByLoginCode(loginCode);
                String userId = userInfo.getId();
                userSecurity = securityClient.createSecurityByUserId(userId);
            }else{
                //result.setErrorMsg("公钥信息已存在。");
                //这里删除原有的公私钥重新分配
                //1-1根据用户登陆名获取用户信息。
                User userInfo = userManager.getUserByLoginCode(loginCode);
                String userId = userInfo.getId();
                String userKeyId = securityClient.getUserKeyByUserId(userId);
                securityClient.deleteSecurity(userSecurity.getId());
                securityClient.deleteUserKey(userKeyId);
                userSecurity = securityClient.createSecurityByUserId(userId);

            }
            String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                    + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");
            keyMap.put("publicKey", userSecurity.getPublicKey());
            keyMap.put("validTime", validTime);
            keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
            return keyMap;
        } catch (Exception ex) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("failed");
            return apiErrorEcho;
        }
    }


    /**
     * 检查用户是否存在
     * @param type
     * @param searchNm
     * @return
     */
    @RequestMapping(value = "/user/isExit")
    public Object searchUser(String type,String searchNm){

        List<User> list = userManager.searchUser(type,searchNm);
        if(list.size()>0){
            return false;
        }else{
            return true;
        }
    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "/loginIndetification" , method = RequestMethod.GET)
    public User loginIndetification(String loginCode,String psw) {
        return  userManager.loginIndetification(loginCode,psw);
    }

    /**
     *
     * 根据loginCode 获取user
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/loginCode" , method = RequestMethod.GET)
    public User getUserByLoginCode(String loginCode) {
        return  userManager.getUserByLoginCode(loginCode);
    }
}
