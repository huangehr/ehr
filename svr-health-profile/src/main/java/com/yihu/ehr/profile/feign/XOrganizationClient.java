package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 11:40
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.Organization)
public interface XOrganizationClient {

    @RequestMapping(value = "/organizations/{org_codes}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码列表批量查询机构")
    List<MOrganization> getOrgs(
            @PathVariable(value = "org_codes") List<String> orgCodes);
}
