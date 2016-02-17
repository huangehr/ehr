package com.yihu.ehr.ha.security.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/2/1.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0 )
@RestController
@Api(value = "sec", description = "安全管理接口，用于安全验证管理", tags = {"安全管理接口"})
public class SecurityController extends BaseRestController {

    @Autowired
    private static SecurityClient securityClient;


    @RequestMapping(value = "/securities/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥" , notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    public MUserSecurity getUserSecurityByLoginCode(
            @ApiParam(name = "login_code", value = "用户名")
            @PathVariable(value = "login_code") String loginCode) throws Exception {
       return securityClient.getUserSecurityByLoginCode(loginCode);
    }



    @RequestMapping(value = "/securities/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    public MUserSecurity getUserSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) {
        return securityClient.getUserSecurityByOrgCode(orgCode);
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
    @RequestMapping(value = "/tokens", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户登录用的临时会话Token",produces = "application/json", notes = "此Token用于客户与平台之间的会话，时效性时差为20分钟")
    public Object getUserToken(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
            @ApiParam(required = true, name = "app_id", value = "APP ID")
            @RequestParam(value = "app_id", required = true) String appId,
            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
            @RequestParam(value = "app_secret", required = true) String appSecret) throws Exception {

        return securityClient.getUserToken(userName,rsaPWD,appId,appSecret);
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
    @RequestMapping(value = "/tokens/{user_id}/{refresh_token}/{app_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "刷新用户临时会话Token" , notes = "若用户的会话Token已失效，调用此方法刷新。")
    public Object refreshToken(
            @ApiParam(required = true, name = "user_id", value = "用户名")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(required = true, name = "refresh_token", value = "已过期的Token")
            @PathVariable(value = "refresh_token") String refreshToken,
            @ApiParam(required = true, name = "app_id", value = "App Id")
            @PathVariable(value = "app_id") String appId) throws Exception {

        return securityClient.refreshToken(userId,refreshToken,appId);
    }

    /**
     * 1-4 当退出登陆时，access_token要做取消处理。
     * requestBody格式:
     * {
     * "access_token": "f67b9646bcdaa60c647dfe7bc26231293847"
     * }
     */
    @RequestMapping(value = "/tokens/{access_token}", method = RequestMethod.DELETE)
    @ApiOperation(value = "作废临时会话Token", notes = "用户或App要退出时，调用此方法作废临时会话Token。")
    public boolean revokeToken(
            @ApiParam(required = true, name = "access_token", value = "要作废的会话Token")
            @PathVariable(value = "access_token") String accessToken) throws Exception {
        return securityClient.revokeToken(accessToken);
    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/securities/{org_code}", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security")
    public MUserSecurity createSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) throws Exception {
        return securityClient.createSecurityByOrgCode(orgCode);
    }

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/user_keys/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据orgCode创建security")
    public Object getUserKeyIdByOrgCd(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) throws Exception {
        return securityClient.getUserKeyIdByOrgCd(orgCode);
    }

    /**
     * 根据id删除security
     * @param id
     */
    @RequestMapping(value = "securities/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除security")
    public boolean deleteSecurity(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable( value = "id") String id) {
        return securityClient.deleteSecurity(id);
    }

    /**
     * UserKey
     * @param userKeyId
     */
    @RequestMapping(value = "/user_keys/{user_key_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除userKey" )
    public boolean deleteUserKey(
            @ApiParam(name = "user_key_id", value = "userKey代码")
            @PathVariable( value = "user_key_id") String userKeyId) {
        return securityClient.deleteUserKey(userKeyId);
    }

    /**
     * 根据loginCode删除Security
     * @param loginCode
     */
    @RequestMapping(value = "/securities/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据loginCode获取Security" )
    public MUserSecurity getUserSecurityByUserName(
            @ApiParam(name = "login_code", value = "用户登录代码")
            @PathVariable( value = "login_code") String loginCode) throws Exception{
        return securityClient.getUserSecurityByUserName(loginCode);
    }

    /**
     * 根据userId创建Security
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/securities/{user_id}", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security" )
    public MUserSecurity createSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) throws Exception {
        return securityClient.createSecurityByUserId(userId);
    }


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user_keys/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取user_key" )
    public Object getUserKeyByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) {
        return securityClient.getUserKeyByUserId(userId);
    }


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/securities/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取UserSecurity" )
    public MUserSecurity getUserSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) {
        return securityClient.getUserSecurityByUserId(userId);
    }
}
