package com.yihu.ehr.security.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.User)
@RequestMapping(value = "/api/v1.0")
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = "users/{user_id}", method = GET )
    MUser getUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = "users/login/{login_code}", method = GET )
    MUser getUserByLoginCode(@PathVariable(value = "login_code") String loginCode);


    @RequestMapping(value = "/users/verification/{login_code}", method = GET )
    MUser loginVerification(
            @PathVariable(value = "login_code") String loginCode,
            @PathVariable(value = "psw") String psw);

}
