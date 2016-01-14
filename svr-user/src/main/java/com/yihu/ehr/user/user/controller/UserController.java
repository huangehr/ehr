package com.yihu.ehr.user.user.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.user.user.model.User;
import com.yihu.ehr.user.user.model.UserDetailModel;
import com.yihu.ehr.user.user.model.UserManager;
import com.yihu.ehr.user.user.model.UserModel;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
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

//    @Resource(name = Services.SecurityManager)
//    private XSecurityManager securityManager;

//    @Resource(name = Services.OrgManager)
//    private XOrgManager orgManager;

//    @ApiParam(name = "searchNm", value = "查询条件", defaultValue = "")
//    @RequestParam(value = "searchNm") Integer searchNm,
//    @ApiParam(name = "catalog", value = "类别", defaultValue = "")
//    @RequestParam(value = "catalog") Integer catalog,
//    @ApiParam(name = "status", value = "状态", defaultValue = "")
//    @RequestParam(value = "status") Integer status,
//    @ApiParam(name = "page", value = "当前页", defaultValue = "")
//    @RequestParam(value = "page") Integer page,
//    @ApiParam(name = "rows", value = "页数", defaultValue = "")
//    @RequestParam(value = "rows") Integer rows

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
            @ApiParam(name = "model", value = "", defaultValue = "")
            @RequestParam(value = "model") Model model,
            @ApiParam(name = "userId", value = "", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "mode", value = "", defaultValue = "")
            @RequestParam(value = "mode") String mode) {
        User user = userManager.getUser(userId);
        UserModel userModel = userManager.getUser(user);
        //OrganizationModel org = orgManager.getOrg(userModel.getOrgCode());
//        XAddress orgLoc = org==null? new Address():org.getLocation();
//        model.addAttribute("orgLoc", orgLoc);
//        model.addAttribute("user",userModel);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage", "user/userInfoDialog");
        return  "simpleView";
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

//    @RequestMapping("distributeKey")
//    @ResponseBody
//    public Object distributeKey(String loginCode) {
//        try {
//            UserSecurityModel userSecurity = securityManager.getUserSecurityByUserName(loginCode);
//            Map<String, String> keyMap = new HashMap<>();
//            if (userSecurity == null) {
//                XUserManager userManager = ServiceFactory.getService(Services.UserManager);
//                XUser userInfo = userManager.getUserByLoginCode(loginCode);
//                String userId = userInfo.getId();
//                userSecurity = securityManager.createSecurityByUserId(userId);
//            }else{
//                //result.setErrorMsg("公钥信息已存在。");
//                //这里删除原有的公私钥重新分配
//                //1-1根据用户登陆名获取用户信息。
//                XUserManager userManager = ServiceFactory.getService(Services.UserManager);
//                XUser userInfo = userManager.getUserByLoginCode(loginCode);
//                String userId = userInfo.getId();
//                String userKeyId = securityManager.getUserKeyByUserId(userId);
//                securityManager.deleteSecurity(userSecurity.getId());
//                securityManager.deleteUserKey(userKeyId);
//                userSecurity = securityManager.createSecurityByUserId(userId);
//
//            }
//                String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
//                        + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
//                keyMap.put("publicKey", userSecurity.getPublicKey());
//                keyMap.put("validTime", validTime);
//                keyMap.put("startTime", DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
//
//            Result result = getSuccessResult(true);
//            result.setObj(keyMap);
//            return result.toJson();
//        } catch (Exception ex) {
//
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        }
//    }

    /**
     * 检查用户是否存在
     * @param type
     * @param searchNm
     * @return
     */
    @RequestMapping("user/isExit")
    @ResponseBody
    public Object searchUser(String type,String searchNm){

        List<User> list = userManager.searchUser(type,searchNm);
        if(list.size()>0){
            return false;
        }else{
            return true;
        }
    }
}
