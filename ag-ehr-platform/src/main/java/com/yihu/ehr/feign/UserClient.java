package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 15:01
 */
@FeignClient(MicroServices.User)
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/users/{login_code}", method = GET)
    MUser getUserByUserName(@PathVariable(value = "login_code") String loginCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/users", method = GET)
    List<MUser> getUsers();



    @RequestMapping(value = ApiVersion.Version1_0 + "/users/login/{login_code}", method = GET)
    MUser getUserLoginCode(@PathVariable(value = "login_code") String loginCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/users/user_name/{user_name}/password/{password}", method = GET)
    MUser getUserByNameAndPassword(
            @PathVariable(value = "user_name") String userName,
            @PathVariable(value = "password") String password);
}
