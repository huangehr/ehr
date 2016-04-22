package com.yihu.ehr.org.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServiceIpAddressStr;
import com.yihu.ehr.constants.MicroServicePort;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MKey;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServices.Security)
@ApiIgnore
public interface SecurityClient {


    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Securities.OrganizationKey, method = RequestMethod.GET)
    MKey getOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) ;

    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Securities.OrganizationKey, method = RequestMethod.POST)
    MKey createOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Securities.deleteOrgKey, method = RequestMethod.DELETE)
    boolean deleteKeyByOrgCode(
            @ApiParam(name = "org_code", value = "org_code")
            @PathVariable(value = "org_code") String orgCode);


}
