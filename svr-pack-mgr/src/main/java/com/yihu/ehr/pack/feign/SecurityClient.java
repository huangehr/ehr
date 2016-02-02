package com.yihu.ehr.pack.feign;

import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 14:58
 */
@FeignClient(MicroServices.Security)
public interface SecurityClient {
    @RequestMapping(value = "/rest/{api_version}/security/login_code", method = GET)
    String getUserSecurityByUserName(@RequestParam(value = "loginCode") String loginCode);
}
