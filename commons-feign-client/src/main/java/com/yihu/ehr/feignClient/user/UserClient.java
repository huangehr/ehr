package com.yihu.ehr.feignClient.user;

import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-user")
@RequestMapping("/rest/v1.0/user")
public interface UserClient {

    @RequestMapping(value = "/user", method = GET ,consumes = "application/json")
    MUser getUser(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/login_code", method = GET ,consumes = "application/json")
    MUser getUserByLoginCode(@RequestParam(value = "loginCode") String loginCode);


    @RequestMapping(value = "/login_indetification", method = GET ,consumes = "application/json")
    MUser loginIndetification(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "psw") String psw);




}
