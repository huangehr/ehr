package com.yihu.ehr.user.feign;

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

    @RequestMapping(value = "/rest/{api_version}/security/{login_code}", method = GET )
    MUserSecurity getUserSecurityByLoginCode(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "login_code") String loginCode);

    @RequestMapping(value = "/rest/{api_version}/security", method = DELETE )
    void deleteSecurity(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/user_key", method = DELETE )
    void deleteUserKey(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "user_key_id") String userKeyId);

    @RequestMapping(value = "/rest/{api_version}/security/{user_id}", method = POST )
    MUserSecurity createSecurityByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = "/rest/{api_version}/security/{user_id}", method = GET )
    MUserSecurity getUserSecurityByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_id") String userId);


    @RequestMapping(value = "/rest/{api_version}/user_key/{user_id}", method = GET )
    String getUserKeyByUserId(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "user_id") String userId);


}
