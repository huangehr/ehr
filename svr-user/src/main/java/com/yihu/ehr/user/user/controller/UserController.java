package com.yihu.ehr.user.user.controller;

import com.yihu.ha.constrant.ErrorCode;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.geography.model.Address;
import com.yihu.ha.geography.model.XAddress;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.organization.model.XOrganization;
import com.yihu.ha.security.model.XSecurityManager;
import com.yihu.ha.security.model.XUserSecurity;
import com.yihu.ha.user.model.*;
import com.yihu.ha.util.controller.BaseController;
import com.yihu.ha.util.operator.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Resource(name = Services.UserManager)
    private XUserManager userManager;

    @Resource(name = Services.SecurityManager)
    private XSecurityManager securityManager;

    @Resource(name = Services.OrgManager)
    private XOrgManager orgManager;

    @RequestMapping("initial")
    public String userInitial(Model model) {
        model.addAttribute("contentPage","user/user");
        return "pageView";
    }

    @RequestMapping("addUserInfoDialog")
    public String addUser(Model model){
        model.addAttribute("contentPage","user/addUserInfoDialog");
        return "generalView";
    }

    @RequestMapping("searchUsers")
    @ResponseBody
    public String searchUsers(String searchNm, String searchType, int page, int rows) {

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("realName", searchNm);
        conditionMap.put("organization", searchNm);
        conditionMap.put("type", searchType);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        List<UserDetailModel> detailModelList = userManager.searchUserDetailModel(conditionMap);

        Integer totalCount = userManager.searchUserInt(conditionMap);
        Result result = getResult(detailModelList, totalCount, page, rows);

        return result.toJson();
    }

    @RequestMapping("deleteUser")
    @ResponseBody
    public String deleteUser(String userId) {

        try {
            userManager.deleteUser(userId);
            Result result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {
            Result result = getSuccessResult(false);
            return result.toJson();
        }
    }

    @RequestMapping("activityUser")
    @ResponseBody
    public String  activityUser(String userId,boolean activated) {

        try {
            userManager.activityUser(userId, activated);
            Result result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {
            Result result = getSuccessResult(false);
            return result.toJson();
        }
    }

    @RequestMapping("updateUser")
    @ResponseBody
    public String updateUser(UserModel userModel) {
        try{
            Map<ErrorCode, String> message = userManager.updateUser(userModel);
            if (message != null) {
                Result result = getSuccessResult(false);
                result.setObj(message);
                return result.toJson();
            }

            Result result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {

            Result result = getSuccessResult(false);
            return result.toJson();
        }

    }

    @RequestMapping("resetPass")
    @ResponseBody
    public String resetPass(String userId) {

        try{
            userManager.resetPass(userId);
            Result result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {

            Result result = getSuccessResult(false);
            return result.toJson();
        }

    }

    @RequestMapping("getUser")
    public String getUser(Model model, String userId, String mode) {

        XUser user = userManager.getUser(userId);
        UserModel userModel = userManager.getUser(user);
        XOrganization org = orgManager.getOrg(userModel.getOrgCode());
        XAddress orgLoc = org==null? new Address():org.getLocation();
        model.addAttribute("orgLoc", orgLoc);
        model.addAttribute("user",userModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage", "user/userInfoDialog");
        return  "simpleView";
    }

    @RequestMapping("unbundling")
    @ResponseBody
    public void unbundling(String userId, String type) {

        XUser user = userManager.getUser(userId);

        if (type.equals("tel")) {
            //tel尚未数据库映射
            user.setEmail("");
        } else {
            user.setEmail("");
        }
        userManager.updateUser(user);
    }

    @RequestMapping("distributeKey")
    @ResponseBody
    public String distributeKey(String loginCode) {
        try {
            XUserSecurity userSecurity = securityManager.getUserSecurityByUserName(loginCode);
            Map<String, String> keyMap = new HashMap<>();
            if (userSecurity == null) {
                XUserManager userManager = ServiceFactory.getService(Services.UserManager);
                XUser userInfo = userManager.getUserByLoginCode(loginCode);
                String userId = userInfo.getId();
                userSecurity = securityManager.createSecurityByUserId(userId);
            }else{
                //result.setErrorMsg("公钥信息已存在。");
                //这里删除原有的公私钥重新分配
                //1-1根据用户登陆名获取用户信息。
                XUserManager userManager = ServiceFactory.getService(Services.UserManager);
                XUser userInfo = userManager.getUserByLoginCode(loginCode);
                String userId = userInfo.getId();
                String userKeyId = securityManager.getUserKeyByUserId(userId);
                securityManager.deleteSecurity(userSecurity.getId());
                securityManager.deleteUserKey(userKeyId);
                userSecurity = securityManager.createSecurityByUserId(userId);

            }
                String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                        + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
                keyMap.put("publicKey", userSecurity.getPublicKey());
                keyMap.put("validTime", validTime);
                keyMap.put("startTime", DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));

            Result result = getSuccessResult(true);
            result.setObj(keyMap);
            return result.toJson();
        } catch (Exception ex) {

            Result result = getSuccessResult(true);
            return result.toJson();
        }
    }

    @RequestMapping("/searchUser")
    @ResponseBody
    public String searchUser(String type,String searchNm){

        boolean bo = userManager.searchUser(type,searchNm);
        if(bo){
            Result result = getSuccessResult(true);
            return result.toJson();
        }else{
            Result result = getSuccessResult(false);
            return result.toJson();
        }
    }
}
