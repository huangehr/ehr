package com.yihu.ehr.security.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServiceName;
import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@ApiIgnore
@FeignClient(name = MicroServiceName.User)
@RequestMapping(ApiVersion.Version1_0)
public interface UserClient {

    @RequestMapping(value = RestApi.Users.User, method = GET)
    MUser getUserByLoginCode(@PathVariable(value = "user_name") String userName);
    
    @RequestMapping(value = RestApi.Users.UserAdmin, method = GET)
    MUser getUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = RestApi.Users.UserPassword, method = GET)
    MUser getUserByNameAndPassword(
            @PathVariable(value = "user_name") String userName,
            @PathVariable(value = "password") String password);

}
