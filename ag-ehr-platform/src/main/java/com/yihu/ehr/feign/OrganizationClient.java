package com.yihu.ehr.feign;

import com.yihu.ehr.api.model.OrganizationModel;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.org.MOrganization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 15:01
 */
@FeignClient(name = MicroServiceName.Organization,url = MicroServiceIpAddressStr.Organization+ MicroServicePort.Organization)
@ApiIgnore
public interface OrganizationClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations/{org_code}", method = GET)
    OrganizationModel getOrg(@PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations/admin/{admin_login_code}", method = GET)
    OrganizationModel getOrgByAdminLoginCode(@PathVariable(value = "admin_login_code") String adminLoginCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations", method = GET)
    List<OrganizationModel> search(
            @RequestParam(value = "fields") String fields,
            @RequestParam(value = "filters") String filters,
            @RequestParam(value = "sorts") String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

}
