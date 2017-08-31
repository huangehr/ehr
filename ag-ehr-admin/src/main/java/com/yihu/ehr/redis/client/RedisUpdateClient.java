package com.yihu.ehr.redis.client;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Created by Sxy on 2017/08/30.
 */
@FeignClient(value = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisUpdateClient {

    @ApiOperation("Redis更新机构名称")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgName, method = RequestMethod.POST)
    Boolean updateOrgName(
            @RequestParam(value = "orgCode") String orgCode);

    @ApiOperation("Redis更新机构区域")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgArea, method = RequestMethod.POST)
    Boolean updateOrgArea(
            @RequestParam(value = "orgCode") String orgCode);


    @ApiOperation("Redis更新机构Saas区域")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgSaasArea, method = RequestMethod.POST)
    Boolean updateOrgSaasArea(
            @RequestParam(value = "orgCode") String orgCode);


    @ApiOperation("Redis更新机构Saas机构")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgSaasOrg, method = RequestMethod.POST)
    Boolean updateOrgSaasOrg(
            @RequestParam(value = "orgCode") String orgCode);

}
