package com.yihu.ehr.security.service;

import com.yihu.ehr.model.user.UserModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-user")
public interface UserClient {


    @RequestMapping(value = "/user/loginIndetification", method = GET ,consumes = "application/json")
    UserModel loginIndetification(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "psw") String psw);

    @RequestMapping(value = "/user/user", method = GET ,consumes = "application/json")
    UserModel getUser(
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/user/user/loginCode", method = GET ,consumes = "application/json")
    UserModel getUserByLoginCode(
            @RequestParam(value = "loginCode") String loginCode);

}
