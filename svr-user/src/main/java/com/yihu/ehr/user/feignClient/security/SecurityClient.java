package com.yihu.ehr.user.feignClient.security;

import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-security")
public interface SecurityClient {

    @RequestMapping(value = "/rest/{api_version}/security/organization_key/org_code", method = GET)
    String getOrgPublicKey(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/org_code", method = POST)
    MUserSecurity createSecurityByOrgCode(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/org_code", method = GET)
    MUserSecurity getUserSecurityByOrgCode(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/user_name", method = GET )
    MUserSecurity getUserSecurityByOrgName(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "userName") String userName);

    @RequestMapping(value = "/rest/{api_version}/security/", method = DELETE )
    void deleteSecurity(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/security/user_key", method = DELETE )
    void deleteUserKey(@RequestParam(value = "userKeyId") String userKeyId);

    @RequestMapping(value = "/rest/{api_version}/security/user_id", method = POST )
    MUserSecurity createSecurityByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/user_id", method = GET )
    MUserSecurity getUserSecurityByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "userId") String userId);


    @RequestMapping(value = "/rest/{api_version}/security/user_key/org_code", method = GET )
    String getUserKeyByOrgCd(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/user_key/user_id", method = GET )
    String getUserKeyByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/login_code", method = GET )
    String getUserSecurityByUserName(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "loginCode") String loginCode);


}
