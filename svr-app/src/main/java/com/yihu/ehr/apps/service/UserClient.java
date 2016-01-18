package com.yihu.ehr.apps.service;

import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-user")
public interface UserClient {

    @RequestMapping(value = "/user/user", method = GET,consumes = "application/json")
    MUser getUser(@RequestParam(value = "id") String userId);










}
