package com.yihu.ehr.security.service;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MKey;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(name=MicroServices.Security)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface SecurityClient {


    @RequestMapping(value = RestApi.Securities.UserKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户Key", notes = "公-私钥用于与健康档案平台加密传输数据使用")
    MKey getUserKey(
            @ApiParam(name = "user_id", value = "用户名")
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = RestApi.Securities.UserKey, method = RequestMethod.POST)
    @ApiOperation(value = "为用户创建Key")
    MKey createUserKey(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable(value = "user_id") String userId) ;

    @RequestMapping(value = RestApi.Securities.UserKeyId, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户key的id")
    String getKeyIdByUserId(
            @ApiParam(name = "user_id", value = "用户id")
            @PathVariable(value = "user_id") String userId) ;


    @RequestMapping(value = RestApi.Securities.deleteUserKey, method = RequestMethod.DELETE)
    @ApiOperation(value = "根据user id删除Key")
    boolean deleteKeyByUserId(
            @ApiParam(name = "user_id", value = "user_id")
            @PathVariable(value = "user_id") String userId);


    @RequestMapping(value = RestApi.Securities.UserToken, method = RequestMethod.PUT)
    @ApiOperation(value = "获取用户登录用的临时会话Token", produces = "application/json", notes = "此Token用于客户与平台之间的会话，时效性时差为20分钟")
    Object createTempUserToken(
            @ApiParam(required = true, name = "user_id", value = "用户名")
            @RequestParam(value = "user_id", required = true) String userId,
            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
            @ApiParam(required = true, name = "app_id", value = "APP ID")
            @RequestParam(value = "app_id", required = true) String appId,
            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
            @RequestParam(value = "app_secret", required = true) String appSecret) ;

    @RequestMapping(value = RestApi.Securities.OrganizationKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取机构Key", produces = "application/json", notes = "公-私钥用于与健康档案平台加密传输数据使用")
    MKey getOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) ;

    @RequestMapping(value = RestApi.Securities.OrganizationKey, method = RequestMethod.POST)
    @ApiOperation(value = "为机构创建Key")
    MKey createOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) ;

    @RequestMapping(value = RestApi.Securities.OrganizationPublicKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取机构公钥")
     String getOrgPublicKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) ;


    @RequestMapping(value = RestApi.Securities.deleteOrgKey, method = RequestMethod.DELETE)
    @ApiOperation(value = "根据org code删除Key")
    boolean deleteKeyByOrgCode(
            @ApiParam(name = "org_code", value = "org_code")
            @PathVariable(value = "org_code") String orgCode) ;


    @RequestMapping(value = RestApi.Securities.Keys, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取Key")
    MKey getKey(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable(value = "id") String id);

}
