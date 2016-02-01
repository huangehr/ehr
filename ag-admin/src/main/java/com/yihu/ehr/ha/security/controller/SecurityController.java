package com.yihu.ehr.ha.security.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/2/1.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/sec")
@RestController
public class SecurityController extends BaseRestController {

    @Autowired
    private static SecurityClient securityClient;

    @RequestMapping(value = "/user_name", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥",  produces = "application/json", notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    public Object getUserSecurityByOrgName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "userName", value = "用户名")
            @RequestParam(value = "userName") String userName) {
       return securityClient.getUserSecurityByOrgName(apiVersion,userName);
    }



    @RequestMapping(value = "/org_code", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    public Object getUserSecurityByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode) {
       return securityClient.getUserSecurityByOrgCode(apiVersion,orgCode);
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
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户登录用的临时会话Token",produces = "application/json", notes = "此Token用于客户与平台之间的会话，时效性时差为20分钟")
    public Object getUserToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
            @ApiParam(required = true, name = "app_id", value = "APP ID")
            @RequestParam(value = "app_id", required = true) String appId,
            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
            @RequestParam(value = "app_secret", required = true) String appSecret){

        return securityClient.getUserToken(apiVersion,userName,rsaPWD,appId,appSecret);
    }

    /**
     * 1-3 access_token失效后，根据传入的用户名及refresh_token重新申请accesee_token。
     * requestBody格式:
     * {
     * "user_id": "hill9868",
     * "refresh_token": "f67b9646bcdaa60c647dfe7bc2623190",
     * "app_id" :"AnG4G4zIz1"
     * }
     */
    @RequestMapping(value = "/token", method = RequestMethod.PUT)
    @ApiOperation(value = "刷新用户临时会话Token",  produces = "application/json", notes = "若用户的会话Token已失效，调用此方法刷新。")
    public Object refreshToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_id", value = "用户名")
            @RequestParam(value = "user_id", required = true) String userId,
            @ApiParam(required = true, name = "refresh_token", value = "已过期的Token")
            @RequestParam(value = "refresh_token", required = true) String refreshToken,
            @ApiParam(required = true, name = "app_id", value = "App Id")
            @RequestParam(value = "app_id", required = true) String appId) {

        return securityClient.refreshToken(apiVersion,userId,refreshToken,appId);
    }

    /**
     * 1-4 当退出登陆时，access_token要做取消处理。
     * requestBody格式:
     * {
     * "access_token": "f67b9646bcdaa60c647dfe7bc26231293847"
     * }
     */
    @RequestMapping(value = "/token", method = RequestMethod.DELETE)
    @ApiOperation(value = "作废临时会话Token",  produces = "application/json", notes = "用户或App要退出时，调用此方法作废临时会话Token。")
    public Object revokeToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "access_token", value = "要作废的会话Token")
            @RequestParam(value = "access_token", required = true) String accessToken){
        return securityClient.revokeToken(apiVersion,accessToken);
    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/org_code", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security",  produces = "application/json")
    public Object createSecurityByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam( value = "orgCode") String orgCode) {
       return securityClient.createSecurityByOrgCode(apiVersion,orgCode);
    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/user_key/org_code", method = RequestMethod.GET)
    @ApiOperation(value = "根据orgCode创建security",  produces = "application/json")
    public String getUserKeyIdByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam( value = "orgCode") String orgCode) {
        return securityClient.getUserKeyIdByOrgCd(apiVersion,orgCode);
    }

    /**
     * 根据id删除security
     * @param id
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除security",  produces = "application/json")
    public Object deleteSecurity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "security代码")
            @RequestParam( value = "id") String id) {
        return securityClient.deleteSecurity(apiVersion,id);
    }

    /**
     * UserKey
     * @param userKeyId
     */
    @RequestMapping(value = "/user_key", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除userKey",  produces = "application/json")
    public Object deleteUserKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userKeyId", value = "userKey代码")
            @RequestParam( value = "userKeyId") String userKeyId) {
        return securityClient.deleteUserKey(apiVersion,userKeyId);
    }

    /**
     * 根据loginCode删除Security
     * @param loginCode
     */
    @RequestMapping(value = "/login_code", method = RequestMethod.GET)
    @ApiOperation(value = "根据loginCode获取Security",  produces = "application/json")
    public Object getUserSecurityByUserName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "用户登录代码")
            @RequestParam( value = "loginCode") String loginCode) {
        return securityClient.getUserSecurityByUserName(apiVersion,loginCode);
    }


    @RequestMapping(value = "/user_id", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security",  produces = "application/json")
    public Object createSecurityByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId) {
        return securityClient.createSecurityByUserId(apiVersion,userId);
    }


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user_key/user_id", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取Security",  produces = "application/json")
    public String getUserKeyByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId) {
        return securityClient.getUserKeyByUserId(apiVersion,userId);
    }


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user_id", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取UserPublicKey",  produces = "application/json")
    public Object getUserSecurityByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId) {
        return securityClient.getUserSecurityByUserId(apiVersion,userId);

    }
}
