package com.yihu.ehr.security.controller;

import com.yihu.ehr.agModel.security.UserSecurityModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.security.service.SecurityClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2016/2/1.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 +"/admin")
@RestController
@Api(value = "sec", description = "安全管理接口，用于安全验证管理", tags = {"安全管理接口"})
public class SecurityController extends BaseController{

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private UserClient userClient;

    /***************************** 用户秘钥信息 cms Start *************************************/
    @RequestMapping(value = "/securities/user/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥" , notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    public Envelop getUserSecurityByLoginCode(
            @ApiParam(name = "login_code", value = "用户名")
            @PathVariable(value = "login_code") String loginCode) throws Exception {

        Envelop envelop = new Envelop();

        MUser mUser = userClient.getUserByUserName(loginCode);
        MKey mKey = securityClient.getUserKey(mUser.getId(),true);
        UserSecurityModel userSecurityModel = convertToUserSecurityModel(mKey);

        if(mKey != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取用户公钥失败");
        }

       return envelop;
    }

    /**
     * 根据userId创建Security
     * @param userId
     * @return
     * @throws Exception
     * 1
     */
    @RequestMapping(value = "/securities/user/{user_id}", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security" )
    public Envelop createSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) throws Exception {

        Envelop envelop = new Envelop();

        MKey mKey = securityClient.createUserKey(userId);
        UserSecurityModel userSecurityModel = convertToUserSecurityModel(mKey);

        if(userSecurityModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建Security失败");
        }

        return envelop;
    }

    /**
     * 根据userId获取UserSecurity
     * @param userId
     * @return
     * 2
     */
    @RequestMapping(value = "/securities/admin/user/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取UserSecurity" )
    public Envelop getUserSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) {

        Envelop envelop = new Envelop();

        MKey mKey = securityClient.getUserKey(userId,true);
        UserSecurityModel userSecurityModel = convertToUserSecurityModel(mKey);

        if(userSecurityModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取UserSecurity失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/securities/user/{user_id}",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据user id 删除用户秘钥" ,produces = "application/json",notes="")
    public Envelop deleteUserKeyByUserId(@PathVariable(value = "user_id")String userId)
    {
        boolean result = securityClient.deleteKeyByUserId(userId);
        if(!result)
        {
            return failed("删除失败!");
        }
        return success(null);
    }

    /***************************** 用户秘钥信息 cms End ***************************************/

    /***************************** 机构秘钥信息 cms Start *************************************/
    @RequestMapping(value = "/securities/org/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    public Envelop getUserSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) {

        Envelop envelop = new Envelop();

        MKey mKey = securityClient.getOrgKey(orgCode);
        UserSecurityModel userSecurityModel = convertToUserSecurityModel(mKey);

        if(userSecurityModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取企业公钥失败");
        }

        return envelop;
    }


    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     * 4
     */

    @RequestMapping(value = "/securities/org/{org_code}", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security")
    public Envelop createSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) throws Exception {

        Envelop envelop = new Envelop();

        MKey mKey = securityClient.createOrgKey(orgCode);
        UserSecurityModel userSecurityModel = convertToUserSecurityModel(mKey);

        if(userSecurityModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建机构security失败");
        }

        return envelop;
    }

    /**
     * 根据id删除security
     * @param orgCode
     * 3
     */
    @RequestMapping(value = "securities/org/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除security")
    public Envelop deleteKeyByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) {

        boolean bo = securityClient.deleteKeyByOrgCode(orgCode);
        if(!bo){
            return failed("删除失败!");
        }
        return success(null);
    }
    /***************************** 机构秘钥信息 cms End ***************************************/

    public UserSecurityModel convertToUserSecurityModel(MKey mKey)
    {
        if(mKey==null)
        {
            return null;
        }

        UserSecurityModel userSecurityModel = convertToModel(mKey,UserSecurityModel.class);
        userSecurityModel.setExpiryDate(DateToString(mKey.getExpiryDate(), AgAdminConstants.DateTimeFormat));
        userSecurityModel.setFromDate(DateToString(mKey.getFromDate(), AgAdminConstants.DateTimeFormat));
        return userSecurityModel;
    }
}