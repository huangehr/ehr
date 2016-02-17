package com.yihu.ehr.user.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.Security)
@RequestMapping(value = "/rest/v1.0")
public interface SecurityClient {

    @RequestMapping(value = "/securities/{login_code}", method = GET )
    MUserSecurity getUserSecurityByLoginCode(
            @PathVariable(value = "login_code") String loginCode);

    @RequestMapping(value = "/securities/{id}", method = DELETE )
    void deleteSecurity(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/user_keys/{user_key_id}", method = DELETE )
    void deleteUserKey(
            @PathVariable(value = "user_key_id") String userKeyId);

    @RequestMapping(value = "/securities/{user_id}", method = POST )
    MUserSecurity createSecurityByUserId(
            @PathVariable(value = "user_id") String userId);

    @RequestMapping(value = "/securities/{user_id}", method = GET )
    MUserSecurity getUserSecurityByUserId(
            @PathVariable(value = "user_id") String userId);


    @RequestMapping(value = "/user_keys/{user_id}", method = GET )
    String getUserKeyByUserId(
            @PathVariable(value = "user_id") String userId);


}
