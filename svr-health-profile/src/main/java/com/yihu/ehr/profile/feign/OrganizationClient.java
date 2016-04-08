package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrganization;
import org.springframework.cloud.netflix.feign.FeignClient;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServices.Organization)
@ApiIgnore
public interface OrganizationClient {


    MOrganization getOrg(String orgCode);
}
