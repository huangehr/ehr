package com.yihu.ehr.ha.users.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient("svr-app")
@RequestMapping("/rest/v1.0/app")
public interface UserClient {
}
