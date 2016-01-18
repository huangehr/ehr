package com.yihu.ehr.security.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.feignClient.app.AppClient;
import com.yihu.ehr.feignClient.user.UserClient;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.security.service.*;
import com.yihu.ehr.security.service.SecurityManager;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.DateUtil;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/security")
@Api(protocols = "https", value = "Security", description = "安全管理接口", tags = {"用户", "企业", "应用", "安全"})
public class SecurityRestController extends BaseRestController {

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AppClient appClient;

    public Object getPublicKey(UserSecurity userSecurity){
        if (userSecurity == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("获取安全用户失败！");
            return apiErrorEcho;
        } else {
            String publicKey = userSecurity.getPublicKey();
            return publicKey;
        }
    }


    @RequestMapping(value = "/user_key/user_name", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥",  produces = "application/json", notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    public Object getUserPublicKeyByOrgName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @PathVariable(value = "user_name") String user_name) throws Exception {
        UserSecurity userSecurity = securityManager.getUserSecurityByUserName(user_name);
        return getPublicKey(userSecurity);
    }



    @RequestMapping(value = "/user_key/org_code", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    public Object getUserPublicKeyByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        UserSecurity userSecurity = securityManager.getUserPublicKeyByOrgCd(orgCode);
        return getPublicKey(userSecurity);
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
            @RequestParam(value = "app_secret", required = true) String appSecret) throws Exception {

        boolean appResult = appClient.validationApp(appId, appSecret);
        if (!appResult) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("应用不存在");
            return apiErrorEcho;
        }

        UserSecurity userSecurity = securityManager.getUserSecurityByUserName(userName);
        String privateKey = userSecurity.getPrivateKey();
        Key priKey = RSA.genPrivateKey(privateKey);
        String psw = RSA.decrypt(rsaPWD, priKey);

        userName = URLDecoder.decode(userName, "UTF-8");
        appId = URLDecoder.decode(appId, "UTF-8");

        MUser user = userClient.loginIndetification(userName, psw);
        if (user == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("用户不存在");
            return apiErrorEcho;
        }

        UserToken userToken = tokenManager.getUserTokenByUserId(user.getId(), appId);

        if (userToken == null) {
            userToken = tokenManager.createUserToken(user.getId(), appId);

            Map<String,Object> map = new HashMap<>();
            map.put("access_token",userToken.getAccessToken());
            map.put("refresh_token",userToken.getRefreshToken());
            map.put("expires_in",userToken.getExpiresIn());
            map.put("token_id",userToken.getTokenId());
            return map;
        }

        Date currentDate = new Date();
        boolean result = DateUtil.isExpire(userToken.getUpdateDate(), currentDate, userToken.getExpiresIn());

        if (result == true) {
            userToken = tokenManager.refreshAccessToken(user.getId(), userToken.getRefreshToken(), appId);
            Map<String,Object> map = new HashMap<>();
            map.put("access_token",userToken.getAccessToken());
            map.put("refresh_token",userToken.getRefreshToken());
            map.put("expires_in",userToken.getExpiresIn());
            return map;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("access_token",userToken.getAccessToken());
        map.put("refresh_token",userToken.getRefreshToken());
        map.put("expires_in",userToken.getExpiresIn());
        return map;
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
            @RequestParam(value = "app_id", required = true) String appId) throws Exception {

        UserToken userToken = tokenManager.refreshAccessToken(userId, refreshToken, appId);
        if (userToken == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("ehr.security.token.refreshError");
            return apiErrorEcho;
        } else {
            Map<String,Object> map = new HashMap<>();
            map.put("access_token",userToken.getAccessToken());
            map.put("refresh_token",userToken.getRefreshToken());
            map.put("expires_in",userToken.getExpiresIn());
            return map;
        }
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
            @RequestParam(value = "access_token", required = true) String accessToken) throws Exception {
        boolean result = tokenManager.revokeToken(accessToken);

        if (result) {
            return "success";
        } else {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("ehr.security.token.revoke");
            return apiErrorEcho;
        }
    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/security/org_code", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security",  produces = "application/json")
    public UserSecurity createSecurityByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam( value = "orgCode") String orgCode) throws Exception {
        UserSecurity userSecurity = securityManager.createSecurityByOrgCode(orgCode);
        return userSecurity;
    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/userKey/orgCode", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security",  produces = "application/json")
    public String getUserKeyByOrgCd(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam( value = "orgCode") String orgCode) throws Exception {
        String userKeyId = securityManager.getUserKeyByOrgCd(orgCode);
        return userKeyId;
    }

    /**
     * 根据id删除security
     * @param id
     */
    @RequestMapping(value = "/security", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除security",  produces = "application/json")
    public void deleteSecurity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "security代码")
            @RequestParam( value = "id") String id) {
        securityManager.deleteSecurity(id);

    }

    /**
     * UserKey
     * @param userKeyId
     */
    @RequestMapping(value = "/user_key", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除userKey",  produces = "application/json")
    public void deleteUserKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userKeyId", value = "userKey代码")
            @RequestParam( value = "userKeyId") String userKeyId) {
        securityManager.deleteUserKey(userKeyId);
    }

    /**
     * 根据loginCode删除Security
     * @param loginCode
     */
    @RequestMapping(value = "/security/login_code", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据loginCode获取Security",  produces = "application/json")
    public Object getUserSecurityByUserName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "用户登录代码")
            @RequestParam( value = "loginCode") String loginCode) throws Exception{
        return securityManager.getUserSecurityByUserName(loginCode);
    }

    /**
     * 根据userId创建Security
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/security/user_id", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security",  produces = "application/json")
    public UserSecurity createSecurityByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId) throws Exception {
        return securityManager.createSecurityByUserId(userId);
    }


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user_key/user_id", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security",  produces = "application/json")
    public String getUserKeyByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId) {
        return securityManager.getUserKeyByUserId(userId);
    }


}