package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.service.RedisUpdateService;
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
 * Redis更新服务
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/redisUpdate")
@Api(value = "Redis更新服务", description = "Redis更新服务")
public class RedisUpdateEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisUpdateService redisUpdateService;

    @ApiOperation("Redis更新机构名称")
    @RequestMapping(value = ServiceApi.Redis.OrgRedis, method = RequestMethod.POST)
    public String updateOrgName(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode) throws Exception {
        boolean isSuccess = redisUpdateService.updateOrgName(orgCode);
        if(!isSuccess) {
            return "Redis更新机构名称失败！";
        }
        return "Redis更新机构名成功！";
    }

    @ApiOperation("Redis更新机构区域")
    @RequestMapping(value = ServiceApi.Redis.OrgAreaRedis, method = RequestMethod.POST)
    public String updateOrgArea(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode) throws Exception {
        boolean isSuccess = redisUpdateService.updateOrgArea(orgCode);
        if(!isSuccess) {
            return "Redis更新机构区域失败！";
        }
        return "Redis更新机构区域成功！";
    }

    @ApiOperation("Redis更新机构Saas区域")
    @RequestMapping(value = ServiceApi.Redis.OrgSaasAreaRedis, method = RequestMethod.POST)
    public String updateOrgSaasArea(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode) throws Exception {
        boolean isSuccess = redisUpdateService.updateOrgSaasArea(orgCode);
        if(!isSuccess) {
            return "Redis更新机构Saas区域失败！";
        }
        return "Redis更新机构Saas区域成功！";
    }

    @ApiOperation("Redis更新机构Saas机构")
    @RequestMapping(value = ServiceApi.Redis.OrgSaasOrgRedis, method = RequestMethod.POST)
    public String updateOrgSaasOrg(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode) throws Exception {
        boolean isSuccess = redisUpdateService.updateOrgSaasOrg(orgCode);
        if(!isSuccess) {
            return "Redis更新机构Saas机构失败！";
        }
        return "Redis更新机构Saas机构成功！";
    }

}
