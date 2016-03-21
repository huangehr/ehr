package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.*;
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
@ApiIgnore
@FeignClient(name = MicroServices.User)
public interface UserClient {

    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Users.Users, method = GET)
    List<MUser> getUsers();

    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Users.User, method = GET)
    MUser getUserByUserName(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Users.UserPassword, method = GET)
    MUser getUserByNameAndPassword(@PathVariable(value = "user_name") String userName,
                                   @PathVariable(value = "password") String password);
}


