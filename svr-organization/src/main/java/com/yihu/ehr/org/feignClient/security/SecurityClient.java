package com.yihu.ehr.org.feignClient.security;

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

    @RequestMapping(value = "/rest/{api_version}/security/organization_key/org_code", method = GET )
    String getOrgPublicKey(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/org_code", method = POST, consumes = "application/json")
    MUserSecurity createSecurityByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/org_code", method = GET )
    MUserSecurity getUserSecurityByOrgCode(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/user_name", method = DELETE  )
    MUserSecurity getUserSecurityByOrgName(@RequestParam(value = "user_name") String user_name);

    @RequestMapping(value = "/rest/{api_version}/security/", method = DELETE  )
    void deleteSecurity(@RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/security/user_key", method = DELETE  )
    void deleteUserKey(@RequestParam(value = "userKeyId") String userKeyId);

    @RequestMapping(value = "/rest/{api_version}/security/user_id", method = POST  )
    MUserSecurity createSecurityByUserId(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/user_id", method = GET  )
    MUserSecurity getUserSecurityByUserId(@RequestParam(value = "userId") String userId);


    @RequestMapping(value = "/rest/{api_version}/security/user_key/org_code", method = GET  )
    String getUserKeyIdByOrgCd(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/user_key/user_id", method = GET  )
    String getUserKeyByUserId(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/login_code", method = GET  )
    String getUserSecurityByUserName(@RequestParam(value = "loginCode") String loginCode);


}
