package com.yihu.ehr.user.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.Security)
@ApiIgnore
public interface SecurityClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/securities/login/{login_code}", method = GET)
    MUserSecurity getUserSecurityByLoginCode(@PathVariable(value = "login_code") String loginCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/securities/{id}", method = DELETE)
    void deleteSecurity(@PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + "/user_keys/{user_key_id}", method = DELETE)
    void deleteUserKey(@PathVariable(value = "user_key_id") String userKeyId);

    @RequestMapping(value = ApiVersion.Version1_0 + "/securities/user/{user_id}", method = POST)
    MUserSecurity createSecurityByUserId(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ApiVersion.Version1_0 + "/securities/user/{user_id}", method = GET)
    MUserSecurity getUserSecurityByUserId(@PathVariable(value = "user_id") String userId);


    @RequestMapping(value = ApiVersion.Version1_0 + "/user_keys/user/{user_id}", method = GET)
    String getUserKeyByUserId(@PathVariable(value = "user_id") String userId);


}
