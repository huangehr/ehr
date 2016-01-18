package com.yihu.ehr.feignClient.security;

import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-security")
public interface SecurityClient {

    @RequestMapping(value = "/security/organization_key/{orgCode}", method = GET,consumes = "application/json")
    String getOrgPublicKey(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security/organization_key/{orgCode}", method = POST, consumes = "application/json")
    MUserSecurity createSecurityByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security/organization_key/{orgCode}", method = GET,consumes = "application/json")
    String getUserKeyByOrgCd(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security/security/{id}", method = DELETE ,consumes = "application/json")
    void deleteSecurity(@RequestParam(value = "id") String id);

    @RequestMapping(value = "/security/userKey/{userKeyId}", method = DELETE ,consumes = "application/json")
    void deleteUserKey(@RequestParam(value = "userKeyId") String userKeyId);

    @RequestMapping(value = "/security/security/{loginCode}", method = DELETE ,consumes = "application/json")
    MUserSecurity getUserSecurityByUserName(@RequestParam(value = "loginCode") String loginCode);

    @RequestMapping(value = "/security/security/{userId}", method = POST ,consumes = "application/json")
    MUserSecurity createSecurityByUserId(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/security/userKey/{userId}", method = GET ,consumes = "application/json")
    String getUserKeyByUserId(@RequestParam(value = "userId") String userId);
}
