package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrganization;
import org.springframework.cloud.netflix.feign.FeignClient;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@FeignClient(name = MicroServices.Organization)
public interface XOrganizationClient {
    MOrganization getOrg(String orgCode);
}
