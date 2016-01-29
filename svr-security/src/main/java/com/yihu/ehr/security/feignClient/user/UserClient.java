package com.yihu.ehr.security.feignClient.user;

import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-user")
public interface UserClient {

    @RequestMapping(value = "/rest/{api_version}/user/user", method = GET )
    MUser getUser(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/user/login_code", method = GET )
    MUser getUserByLoginCode(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "loginCode") String loginCode);


    @RequestMapping(value = "/rest/{api_version}/user/login_indetification", method = GET )
    MUser loginIndetification(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "psw") String psw);

}
