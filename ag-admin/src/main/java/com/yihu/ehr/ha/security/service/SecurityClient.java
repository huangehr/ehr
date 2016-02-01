package com.yihu.ehr.ha.security.service;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-security")
public interface SecurityClient {

    @RequestMapping(value = "/rest/{api_version}/security/user_name", method = RequestMethod.GET)
    Object getUserSecurityByOrgName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "userName", value = "用户名")
            @RequestParam(value = "userName") String userName);

    @RequestMapping(value = "/rest/{api_version}/security/org_code", method = RequestMethod.GET)
    Object getUserSecurityByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/token", method = RequestMethod.GET)
    Object getUserToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
            @ApiParam(required = true, name = "app_id", value = "APP ID")
            @RequestParam(value = "app_id", required = true) String appId,
            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
            @RequestParam(value = "app_secret", required = true) String appSecret);

    @RequestMapping(value = "/rest/{api_version}/security/token", method = RequestMethod.PUT)
    Object refreshToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_id", value = "用户名")
            @RequestParam(value = "user_id", required = true) String userId,
            @ApiParam(required = true, name = "refresh_token", value = "已过期的Token")
            @RequestParam(value = "refresh_token", required = true) String refreshToken,
            @ApiParam(required = true, name = "app_id", value = "App Id")
            @RequestParam(value = "app_id", required = true) String appId);

    @RequestMapping(value = "/rest/{api_version}/security/token", method = RequestMethod.DELETE)
    Object revokeToken(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "access_token", value = "要作废的会话Token")
            @RequestParam(value = "access_token", required = true) String accessToken);

    @RequestMapping(value = "/rest/{api_version}/security/org_code", method = RequestMethod.POST)
    Object createSecurityByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam( value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/user_key/org_code", method = RequestMethod.GET)
    String getUserKeyIdByOrgCd(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam( value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/", method = RequestMethod.DELETE)
    Object deleteSecurity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "security代码")
            @RequestParam( value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/security/user_key", method = RequestMethod.DELETE)
    Object deleteUserKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userKeyId", value = "userKey代码")
            @RequestParam( value = "userKeyId") String userKeyId);

    @RequestMapping(value = "/rest/{api_version}/security/login_code", method = RequestMethod.GET)
    Object getUserSecurityByUserName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "loginCode", value = "用户登录代码")
            @RequestParam( value = "loginCode") String loginCode);

    @RequestMapping(value = "/rest/{api_version}/security/user_id", method = RequestMethod.POST)
    Object createSecurityByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/user_key/user_id", method = RequestMethod.GET)
    String getUserKeyByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/user_id", method = RequestMethod.GET)
    Object getUserSecurityByUserId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userId", value = "用户代码")
            @RequestParam( value = "userId") String userId);

}
