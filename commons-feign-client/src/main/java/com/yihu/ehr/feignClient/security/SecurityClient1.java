package com.yihu.ehr.feignClient.security;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-securityEhr")
@RequestMapping("/rest/v1.0/securityEhr")
public interface SecurityClient1 {

    @RequestMapping(value = "/test", method = GET ,consumes = "application/json")
    String test();



}
