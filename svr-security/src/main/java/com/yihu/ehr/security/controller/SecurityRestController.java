package com.yihu.ehr.security.controller;

import com.yihu.ehr.security.model.TokenManager;
import com.yihu.ehr.security.model.SecurityManager;
import com.yihu.ehr.security.model.UserSecurity;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.security.Key;
import java.util.Date;

@RestController()
@RequestMapping("/security")
@Api(protocols = "https", value = "Security", description = "安全管理接口", tags = {"用户", "企业", "应用", "安全"})
public class SecurityRestController extends BaseRestController {

    @Autowired
    private SecurityManager securityManager;

//    @Autowired
//    private UserManager userManager;

    @Autowired
    private TokenManager tokenManager;

//    @Autowired
//    private AppManager appManager;

    @RequestMapping(value = "/user_key/{user_name}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥",  produces = "application/json", notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    public Object getUserPublicKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @PathVariable(value = "user_name") String user_name) throws Exception {
        UserSecurity userSecurity = securityManager.getUserSecurityByUserName(user_name);
        if (userSecurity == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("获取安全用户失败！");
            return apiErrorEcho;
        } else {
            String publicKey = userSecurity.getPublicKey();

            return publicKey;
        }
    }

    @RequestMapping(value = "/organization_key/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    public Object getOrgPublicKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) {
        UserSecurity userSecurity = securityManager.getUserPublicKeyByOrgCd(orgCode);
        if (userSecurity == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("获取安全用户失败！");
        } else {
            String publicKey = userSecurity.getPublicKey();
            return publicKey;
        }
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

        boolean appResult = appManager.validationApp(appId, appSecret);
        if (!appResult) {
            RestEcho restEcho = new RestEcho().failed(ErrorCode.InvalidAppRegister);
            return restEcho;
        }

        XUserSecurity userSecurity = securityManager.getUserSecurityByUserName(userName);
        String privateKey = userSecurity.getPrivateKey();
        Key priKey = RSA.genPrivateKey(privateKey);
        String psw = RSA.decrypt(rsaPWD, priKey);

        userName = URLDecoder.decode(userName, "UTF-8");
        appId = URLDecoder.decode(appId, "UTF-8");

        XUser user = userManager.loginIndetification(userName, psw);
        if (user == null) {
            RestEcho restEcho = new RestEcho().failed(ErrorCode.InvalidUserNameOrPwd);
            return restEcho;
        }

        XApp app = appManager.getApp(appId);
        XUserToken userToken = tokenManager.getUserTokenByUserId(user.getId(), appId);

        if (userToken == null) {
            userToken = tokenManager.createUserToken(user, app);

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResult("access_token", userToken.getAccessToken());
            restEcho.putResult("refresh_token", userToken.getRefreshToken());
            restEcho.putResult("expires_in", userToken.getExpiresIn());
            restEcho.putResult("token_id", userToken.getTokenId());

            return restEcho;
        }

        Date currentDate = new Date();
        boolean result = DateUtil.isExpire(userToken.getUpdateDate(), currentDate, userToken.getExpiresIn());

        if (result == true) {
            userToken = tokenManager.refreshAccessToken(user.getId(), userToken.getRefreshToken(), appId);

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResult("access_token", userToken.getAccessToken());
            restEcho.putResult("refresh_token", userToken.getRefreshToken());
            restEcho.putResult("expires_in", userToken.getExpiresIn());

            return restEcho;
        }

        RestEcho restEcho = new RestEcho().success();
        restEcho.putResult("access_token", userToken.getAccessToken());
        restEcho.putResult("refresh_token", userToken.getRefreshToken());
        restEcho.putResult("expires_in", userToken.getExpiresIn());

        return restEcho;
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
    @ApiOperation(value = "刷新用户临时会话Token", response = RestEcho.class, produces = "application/json", notes = "若用户的会话Token已失效，调用此方法刷新。")
    public Object refreshToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_id", value = "用户名")
            @RequestParam(value = "user_id", required = true) String userId,
            @ApiParam(required = true, name = "refresh_token", value = "已过期的Token")
            @RequestParam(value = "refresh_token", required = true) String refreshToken,
            @ApiParam(required = true, name = "app_id", value = "App Id")
            @RequestParam(value = "app_id", required = true) String appId) throws Exception {

        XUserToken userToken = tokenManager.refreshAccessToken(userId, refreshToken, appId);
        if (userToken == null) {
            RestEcho restEcho = new RestEcho().failed(ErrorCode.UserRefreshTokenError);

            return restEcho;
        } else {
            RestEcho restEcho = new RestEcho().success();
            restEcho.putResult("access_token", userToken.getAccessToken());
            restEcho.putResult("refresh_token", userToken.getRefreshToken());
            restEcho.putResult("expires_in", userToken.getExpiresIn());

            return restEcho;
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
    @ApiOperation(value = "作废临时会话Token", response = RestEcho.class, produces = "application/json", notes = "用户或App要退出时，调用此方法作废临时会话Token。")
    public Object revokeToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "access_token", value = "要作废的会话Token")
            @RequestParam(value = "access_token", required = true) String accessToken) throws Exception {
        boolean result = tokenManager.revokeToken(accessToken);

        if (result) {
            return succeed("{\"status\", \"ok\"}");
        } else {
            RestEcho restEcho = new RestEcho().failed(ErrorCode.UserRevokeTokenFailed);
            return restEcho;
        }
    }
}