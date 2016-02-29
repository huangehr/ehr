package com.yihu.ehr.ha.security.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.security.MUserSecurity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-security")
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface SecurityClient {


    @RequestMapping(value = "/securities/login/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥" , notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    MUserSecurity getUserSecurityByLoginCode(
            @ApiParam(name = "login_code", value = "用户名")
            @PathVariable(value = "login_code") String loginCode) ;



    @RequestMapping(value = "/securities/org/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = "application/json", notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    MUserSecurity getUserSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode);

//    @RequestMapping(value = "/tokens", method = RequestMethod.GET)
//    @ApiOperation(value = "获取用户登录用的临时会话Token",produces = "application/json", notes = "此Token用于客户与平台之间的会话，时效性时差为20分钟")
//    Object getUserToken(
//            @ApiParam(required = true, name = "user_name", value = "用户名")
//            @RequestParam(value = "user_name", required = true) String userName,
//            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
//            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
//            @ApiParam(required = true, name = "app_id", value = "APP ID")
//            @RequestParam(value = "app_id", required = true) String appId,
//            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
//            @RequestParam(value = "app_secret", required = true) String appSecret);

//    /**
//     * 1-3 access_token失效后，根据传入的用户名及refresh_token重新申请accesee_token。
//     * requestBody格式:
//     * {
//     * "user_id": "hill9868",
//     * "refresh_token": "f67b9646bcdaa60c647dfe7bc2623190",
//     * "app_id" :"AnG4G4zIz1"
//     * }
//     */
//    @RequestMapping(value = "/tokens/{user_id}/{refresh_token}/{app_id}", method = RequestMethod.PUT)
//    @ApiOperation(value = "刷新用户临时会话Token" , notes = "若用户的会话Token已失效，调用此方法刷新。")
//    Object refreshToken(
//            @ApiParam(required = true, name = "user_id", value = "用户名")
//            @PathVariable(value = "user_id") String userId,
//            @ApiParam(required = true, name = "refresh_token", value = "已过期的Token")
//            @PathVariable(value = "refresh_token") String refreshToken,
//            @ApiParam(required = true, name = "app_id", value = "App Id")
//            @PathVariable(value = "app_id") String appId) ;

//    /**
//     * 1-4 当退出登陆时，access_token要做取消处理。
//     * requestBody格式:
//     * {
//     * "access_token": "f67b9646bcdaa60c647dfe7bc26231293847"
//     * }
//     */
//    @RequestMapping(value = "/tokens/{access_token}", method = RequestMethod.DELETE)
//    @ApiOperation(value = "作废临时会话Token", notes = "用户或App要退出时，调用此方法作废临时会话Token。")
//    boolean revokeToken(
//            @ApiParam(required = true, name = "access_token", value = "要作废的会话Token")
//            @PathVariable(value = "access_token") String accessToken);

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/securities/org/{org_code}", method = RequestMethod.POST)
    @ApiOperation(value = "根据orgCode创建security")
    MUserSecurity createSecurityByOrgCode(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) ;

    /**
     * 根据orgCode创建security
     * @param orgCode
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/user_keys/org/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据orgCode获取security")
    String getUserKeyIdByOrgCd(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable( value = "org_code") String orgCode) ;

    /**
     * 根据id删除security
     * @param id
     */
    @RequestMapping(value = "/securities/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除security")
    boolean deleteSecurity(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable( value = "id") String id) ;

    /**
     * UserKey
     * @param userKeyId
     */
    @RequestMapping(value = "/user_keys/{user_key_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除userKey" )
    boolean deleteUserKey(
            @ApiParam(name = "user_key_id", value = "userKey代码")
            @PathVariable( value = "user_key_id") String userKeyId) ;

    /**
     * 根据loginCode删除Security
     * @param loginCode
     */
//    @RequestMapping(value = "/securities/user/{login_code}", method = RequestMethod.GET)
//    @ApiOperation(value = "根据loginCode获取Security" )
//    MUserSecurity getUserSecurityByUserName(
//            @ApiParam(name = "login_code", value = "用户登录代码")
//            @PathVariable( value = "login_code") String loginCode) ;

    /**
     * 根据userId创建Security
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/securities/user/{user_id}", method = RequestMethod.POST)
    @ApiOperation(value = "根据userId创建Security" )
    MUserSecurity createSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) ;


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user_keys/user/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取user_key" )
    String getUserKeyByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) ;


    /**
     * 根据userId获取UserKey
     * @param userId
     * @return
     */
    @RequestMapping(value = "/securities/user/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取UserSecurity" )
    MUserSecurity getUserSecurityByUserId(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable( value = "user_id") String userId) ;

}
