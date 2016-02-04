package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.Security)
public interface SecurityClient {

    @RequestMapping(value = "/rest/{api_version}/security/{org_code}", method = POST, consumes = "application/json")
    MUserSecurity createSecurityByOrgCode(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/{org_code}", method = GET )
    MUserSecurity getUserSecurityByOrgCode(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/security/{id}", method = DELETE  )
    void deleteSecurity(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/user_key/{user_key_id}", method = DELETE  )
    void deleteUserKey(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_key_id") String userKeyId);

    @RequestMapping(value = "/rest/{api_version}/security/{user_id}", method = POST  )
    MUserSecurity createSecurityByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/{user_id}", method = GET  )
    MUserSecurity getUserSecurityByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = "/rest/{api_version}/user_key/{org_code}", method = GET  )
    String getUserKeyIdByOrgCd(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/user_key/{user_id}", method = GET  )
    String getUserKeyByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/{login_code}", method = GET  )
    String getUserSecurityByLoginCode(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "login_code") String loginCode);
}
