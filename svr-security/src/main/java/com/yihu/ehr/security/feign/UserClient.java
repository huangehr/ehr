package com.yihu.ehr.security.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServiceName.User,url = MicroServiceIpAddressStr.User+ MicroServicePort.User)
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/users/{user_id}", method = GET )
    MUser getUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ApiVersion.Version1_0+"/users/login/{login_code}", method = GET )
    MUser getUserByLoginCode(@PathVariable(value = "login_code") String loginCode);


    @RequestMapping(value = ApiVersion.Version1_0+"/users/user_name/{user_name}/password/{password}", method = GET )
    MUser getUserByNameAndPassword(
            @PathVariable(value = "user_name") String userName,
            @PathVariable(value = "password") String password);

}
