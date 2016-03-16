package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MKey;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 14:58
 */
@FeignClient(MicroServices.Security)
@ApiIgnore
public interface SecurityClient {
    @RequestMapping(value = ApiVersion.Version1_0+"/securities/user/{login_code}", method = GET)
    MKey getUserKey(@PathVariable(value = "login_code") String loginCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/securities/org/{org_code}", method = GET)
    MKey getOrgKey(@PathVariable(value = "org_code") String orgCode);
}
