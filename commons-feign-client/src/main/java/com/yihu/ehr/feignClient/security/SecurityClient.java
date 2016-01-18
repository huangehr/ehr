package com.yihu.ehr.feignClient.security;

import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-secutity")
@RequestMapping("/rest/v1.0/secutity")
public interface SecurityClient {

    @RequestMapping(value = "/organization_key/org_code", method = GET,consumes = "application/json")
    String getOrgPublicKey(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/org_code", method = POST, consumes = "application/json")
    MUserSecurity createSecurityByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/userKey/org_code", method = GET,consumes = "application/json")
    String getUserPublicKeyByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/security", method = DELETE ,consumes = "application/json")
    void deleteSecurity(@RequestParam(value = "id") String id);

    @RequestMapping(value = "/user_key", method = DELETE ,consumes = "application/json")
    void deleteUserKey(@RequestParam(value = "userKeyId") String userKeyId);

    @RequestMapping(value = "/user_key/user_name", method = DELETE ,consumes = "application/json")
    MUserSecurity getUserPublicKeyByOrgName(@RequestParam(value = "user_name") String user_name);

    @RequestMapping(value = "/security/user_id", method = POST ,consumes = "application/json")
    MUserSecurity createSecurityByUserId(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/user_key/user_id", method = GET ,consumes = "application/json")
    String getUserKeyByUserId(@RequestParam(value = "userId") String userId);


}
