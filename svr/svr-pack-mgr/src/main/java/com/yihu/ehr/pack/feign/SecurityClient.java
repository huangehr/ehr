package com.yihu.ehr.pack.feign;

import com.yihu.ehr.constants.ServiceApi;
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
@ApiIgnore
@FeignClient(value = MicroServices.Basic)
public interface SecurityClient {
    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Securities.UserKey, method = GET)
    MKey getUserKey(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Securities.OrganizationKey, method = GET)
    MKey getOrgKey(@PathVariable(value = "org_code") String orgCode);
}
