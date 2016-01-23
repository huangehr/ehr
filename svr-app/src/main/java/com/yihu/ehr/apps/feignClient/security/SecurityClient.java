package com.yihu.ehr.apps.feignClient.security;

import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-security")
@RequestMapping("/rest/v1.0/security")
public interface SecurityClient {

    @RequestMapping(value = "/organization_key/org_code", method = GET,consumes = "application/json")
    String getOrgPublicKey(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security/org_code", method = POST, consumes = "application/json")
    MUserSecurity createSecurityByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security/org_code", method = GET,consumes = "application/json")
    MUserSecurity getUserSecurityByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security/user_name", method = DELETE ,consumes = "application/json")
    MUserSecurity getUserSecurityByOrgName(@RequestParam(value = "user_name") String user_name);

    @RequestMapping(value = "/security", method = DELETE ,consumes = "application/json")
    void deleteSecurity(@RequestParam(value = "id") String id);

    @RequestMapping(value = "/user_key", method = DELETE ,consumes = "application/json")
    void deleteUserKey(@RequestParam(value = "userKeyId") String userKeyId);

    @RequestMapping(value = "/security/user_id", method = POST ,consumes = "application/json")
    MUserSecurity createSecurityByUserId(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/security/user_id", method = GET ,consumes = "application/json")
    MUserSecurity getUserSecurityByUserId(@RequestParam(value = "userId") String userId);


    @RequestMapping(value = "/user_key/org_code", method = GET ,consumes = "application/json")
    String getUserKeyByOrgCd(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/user_key/user_id", method = GET ,consumes = "application/json")
    String getUserKeyByUserId(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/security/login_code", method = GET ,consumes = "application/json")
    String getUserSecurityByUserName(@RequestParam(value = "loginCode") String loginCode);


}
