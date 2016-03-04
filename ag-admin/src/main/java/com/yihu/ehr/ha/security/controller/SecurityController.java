package com.yihu.ehr.ha.security.controller;

import com.yihu.ehr.agModel.security.UserSecurityModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.model.security.MUserSecurity;
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


    @RequestMapping(value = "/securities/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥" , notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    public Envelop getUserSecurityByLoginCode(
            @ApiParam(name = "login_code", value = "用户名")
            @PathVariable(value = "login_code") String loginCode) throws Exception {

        Envelop envelop = new Envelop();

        MUserSecurity mUserSecurity = securityClient.getUserSecurityByLoginCode(loginCode);
        UserSecurityModel userSecurityModel = convertToModel(mUserSecurity,UserSecurityModel.class);

        if(mUserSecurity != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取用户公钥失败");
        }

       return envelop;
    }



    @RequestMapping(value = "/securities/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    public Envelop getUserSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) {

        Envelop envelop = new Envelop();

        MUserSecurity mUserSecurity = securityClient.getUserSecurityByOrgCode(orgCode);
        UserSecurityModel userSecurityModel = convertToModel(mUserSecurity,UserSecurityModel.class);

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
     * 1-2 根据传入的用户名及密码，进行用户验证，并返回生成的token。
     * 1-2-1 对传入的密码进行解密处理
     * 1-2-2 对用户的帐户名及密码进行验证，验证通过，则进入1-2-3，否则提示报错。
     * 1-2-3 根据用户ID及APP ID查询相应的授权信息
     * 1-2-3-1 存在用户授权，判断用户授权是否过期，是则提示过期
     * 1-2-3-2 不存在用户授权，新增授权信息，并返回
     * <p>
     * requestBody格式:
     * {
     * "user_name": "hill9868",
     * "rsa_pw": "f67b9646bcdaa60c647dfe7bc2623190...(略)",
     * "app_id": "AnG4G4zIz1",
     * "app_secret": "mPTZvuEFB6C32fko"
     * }
     *
     */
//    @RequestMapping(value = "/tokens", method = RequestMethod.GET)
//    @ApiOperation(value = "获取用户登录用的临时会话Token",produces = "application/json", notes = "此Token用于客户与平台之间的会话，时效性时差为20分钟")
//    public Envelop getUserToken(
//            @ApiParam(required = true, name = "user_name", value = "用户名")
//            @RequestParam(value = "user_name", required = true) String userName,
//            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
//            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
//            @ApiParam(required = true, name = "app_id", value = "APP ID")
//            @RequestParam(value = "app_id", required = true) String appId,
//            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
//            @RequestParam(value = "app_secret", required = true) String appSecret) throws Exception {
//
//        Envelop envelop = new Envelop();
//
//        UserSecurityModel userSecurityModel = (UserSecurityModel) securityClient.getUserToken(userName,rsaPWD,appId,appSecret);
//
//        if(userSecurityModel != null){
//            envelop.setSuccessFlg(true);
//            envelop.setObj(userSecurityModel);
//        }else {
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("用户登录临时会话Token获取失败");
//        }
//
//        return envelop;
//    }

    /**
     * 1-3 access_token失效后，根据传入的用户名及refresh_token重新申请accesee_token。
     * requestBody格式:
     * {
     * "user_id": "hill9868",
     * "refresh_token": "f67b9646bcdaa60c647dfe7bc2623190",
     * "app_id" :"AnG4G4zIz1"
     * }
     */
//    @RequestMapping(value = "/tokens/{user_id}/{refresh_token}/{app_id}", method = RequestMethod.PUT)
//    @ApiOperation(value = "刷新用户临时会话Token" , notes = "若用户的会话Token已失效，调用此方法刷新。")
//    public Envelop refreshToken(
//            @ApiParam(required = true, name = "user_id", value = "用户名")
//            @PathVariable(value = "user_id") String userId,
//            @ApiParam(required = true, name = "refresh_token", value = "已过期的Token")
//            @PathVariable(value = "refresh_token") String refreshToken,
//            @ApiParam(required = true, name = "app_id", value = "App Id")
//            @PathVariable(value = "app_id") String appId) throws Exception {
//
//        Envelop envelop = new Envelop();
//
//        UserSecurityModel userSecurityModel = (UserSecurityModel)securityClient.refreshToken(userId,refreshToken,appId);
//
//        if(userSecurityModel != null){
//            envelop.setSuccessFlg(true);
//            envelop.setObj(userSecurityModel);
//        }else {
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("刷新用户临时会话Token失败");
//        }
//
//        return envelop;
//    }

    /**
     * 1-4 当退出登陆时，access_token要做取消处理。
     * requestBody格式:
     * {
     * "access_token": "f67b9646bcdaa60c647dfe7bc26231293847"
     * }
     */
//    @RequestMapping(value = "/tokens/{access_token}", method = RequestMethod.DELETE)
//    @ApiOperation(value = "作废临时会话Token", notes = "用户或App要退出时，调用此方法作废临时会话Token。")
//    public Envelop revokeToken(
//            @ApiParam(required = true, name = "access_token", value = "要作废的会话Token")
//            @PathVariable(value = "access_token") String accessToken) throws Exception {
//
//        Envelop envelop = new Envelop();
//
//        Boolean bo = securityClient.revokeToken(accessToken);
//
//        envelop.setSuccessFlg(true);
//        if(!bo){
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("作废临时会话Token失败");
//        }
//
//        return envelop;
//    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     * 4
     */

    @RequestMapping(value = "/securities/{org_code}", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security")
    public Envelop createSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) throws Exception {

        Envelop envelop = new Envelop();

        MUserSecurity mUserSecurity = securityClient.createSecurityByOrgCode(orgCode);
        UserSecurityModel userSecurityModel = convertToModel(mUserSecurity,UserSecurityModel.class);

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
     * 根据orgCode获取security
     * @param orgCode
     * @return
     * @throws Exception
     * 5
     */

//    @RequestMapping(value = "/security/{org_code}", method = RequestMethod.GET)
//    @ApiOperation(value = "根据orgCode获取security")
//    public String getUsersecurityIdByOrgCd(
//            @ApiParam(name = "org_code", value = "机构代码")
//            @PathVariable( value = "org_code") String orgCode) throws Exception {
//
//        String orgSecurity = securityClient.getUsersecurityIdByOrgCd(orgCode);
//
//        return orgSecurity;
//    }

    /**
     * 根据orgCode获取userkey
     * @param orgCode
     * @return
     * @throws Exception
     * 5
     */

    @RequestMapping(value = "/user_keys/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据orgCode获取security")
    public String getUserKeyIdByOrgCd(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) throws Exception {

        String orgSecurity = securityClient.getUserKeyIdByOrgCd(orgCode);

        return orgSecurity;
    }

    /**
     * 根据id删除security
     * @param id
     * 3
     */
    @RequestMapping(value = "securities/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除security")
    public Envelop deleteSecurity(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable( value = "id") String id) {

        Envelop envelop = new Envelop();

        Boolean bo = securityClient.deleteSecurity(id);

        envelop.setSuccessFlg(true);
        if(!bo){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("security删除失败");
        }

        return envelop;
    }

    /**
     * UserKey
     * @param userKeyId
     * 2
     */
    @RequestMapping(value = "/user_keys/{user_key_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除userKey" )
    public Envelop deleteUserKey(
            @ApiParam(name = "user_key_id", value = "userKey代码")
            @PathVariable( value = "user_key_id") String userKeyId) {

        Envelop envelop = new Envelop();

        Boolean bo = securityClient.deleteUserKey(userKeyId);

        envelop.setSuccessFlg(true);
        if(!bo){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("userKey删除失败");
        }
        return envelop;
    }

    /**
     * 根据loginCode获取Security
     * @param loginCode
     * 2-1
     */
//    @RequestMapping(value = "/securities/user/{login_code}", method = RequestMethod.GET)
//    @ApiOperation(value = "根据loginCode获取Security" )
//    public Envelop getUserSecurityByUserName(
//            @ApiParam(name = "login_code", value = "用户登录代码")
//            @PathVariable( value = "login_code") String loginCode) throws Exception{
//
//        Envelop envelop = new Envelop();
//
//        MUserSecurity mUserSecurity = securityClient.getUserSecurityByUserName(loginCode);
//
//        if(mUserSecurity != null){
//            envelop.setSuccessFlg(true);
//            envelop.setObj(mUserSecurity);
//        }else {
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("获取Security失败");
//        }
//
//        return envelop;
//    }

    /**
     * 根据userId创建Security
     * @param userId
     * @return
     * @throws Exception
     * 1
     */
    @RequestMapping(value = "/securities/{user_id}", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security" )
    public Envelop createSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) throws Exception {

        Envelop envelop = new Envelop();

        MUserSecurity mUserSecurity = securityClient.createSecurityByUserId(userId);
        UserSecurityModel userSecurityModel = convertToModel(mUserSecurity,UserSecurityModel.class);

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
     * 根据userId获取UserKey
     * @param userId
     * @return
     *1
     */
    @RequestMapping(value = "/user_keys/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取user_key" )
    public String getUserKeyByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) {

        String userKeyId = securityClient.getUserKeyByUserId(userId);

        return userKeyId;

    }


    /**
     * 根据userId获取UserSecurity
     * @param userId
     * @return
     * 2
     */
    @RequestMapping(value = "/securities/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取UserSecurity" )
    public Envelop getUserSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) {

        Envelop envelop = new Envelop();

        MUserSecurity mUserSecurity = securityClient.getUserSecurityByUserId(userId);
        UserSecurityModel userSecurityModel = convertToModel(mUserSecurity,UserSecurityModel.class);

        if(userSecurityModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userSecurityModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取UserSecurity失败");
        }

        return envelop;
    }
}