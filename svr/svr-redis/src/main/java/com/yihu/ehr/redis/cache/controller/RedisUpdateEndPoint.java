package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.service.RedisUpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sxy on 2017/08/28.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RedisUpdate", description = "Redis更新服务")
public class RedisUpdateEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisUpdateService redisUpdateService;

    @ApiOperation("Redis更新机构名称")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgName, method = RequestMethod.POST)
    public Boolean updateOrgName(
            @ApiParam(name = "orgCode", value = "机构编码(orgCode)")
            @RequestParam(value = "orgCode") String orgCode) {
        return redisUpdateService.updateOrgName(orgCode);
    }

    @ApiOperation("Redis更新机构区域")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgArea, method = RequestMethod.POST)
    public Boolean updateOrgArea(
            @ApiParam(name = "orgCode", value = "机构编码(orgCode)")
            @RequestParam(value = "orgCode") String orgCode) {
        return redisUpdateService.updateOrgArea(orgCode);
    }

    @ApiOperation("Redis更新机构Saas区域")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgSaasArea, method = RequestMethod.POST)
    public Boolean updateOrgSaasArea(
            @ApiParam(name = "orgCode", value = "机构编码(orgCode)")
            @RequestParam(value = "orgCode") String orgCode) {
        return redisUpdateService.updateOrgSaasArea(orgCode);
    }

    @ApiOperation("Redis更新机构Saas机构")
    @RequestMapping(value = ServiceApi.Redis.UpdateOrgSaasOrg, method = RequestMethod.POST)
    public Boolean updateOrgSaasOrg(
            @ApiParam(name = "orgCode", value = "机构编码(orgCode)")
            @RequestParam(value = "orgCode") String orgCode) {
        return redisUpdateService.updateOrgSaasOrg(orgCode);
    }

}
