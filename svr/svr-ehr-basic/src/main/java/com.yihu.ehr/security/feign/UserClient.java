package com.yihu.ehr.security.feign;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@ApiIgnore
@FeignClient(name = MicroServices.User)
public interface UserClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Users.User, method = GET)
    MUser getUserByLoginCode(@PathVariable(value = "user_name") String userName);
    
    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Users.UserAdmin, method = GET)
    MUser getUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Users.UserVerification, method = GET)
    MUser getUserByNameAndPassword(
            @RequestParam(value = "user_name") String userName,
            @RequestParam(value = "password") String password);

}
