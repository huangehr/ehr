package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.service.RedisInitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author hzp add at 20170425
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0+"/redisInit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "Redis初始化服务", description = "Redis初始化服务")
public class RedisInitEndPoint extends EnvelopRestEndPoint {

    @Autowired
    RedisInitService redisInitService;


    @ApiOperation("缓存行政地址Redis")
    @RequestMapping(value = ServiceApi.Redis.AddressRedis, method = RequestMethod.POST)
    @ResponseBody
    public String cacheAddressDict() throws Exception
    {
        redisInitService.cacheAddressDict();
        return "缓存行政地址Redis完成！";
    }

    @ApiOperation("缓存健康问题Redis")
    @RequestMapping(value = ServiceApi.Redis.HealthProblemRedis, method = RequestMethod.POST)
    @ResponseBody
    public String cacheIcd10ByHpCode() throws Exception
    {
        redisInitService.cacheIcd10ByHpCode();
        return "缓存健康问题Redis完成！";
    }

    @ApiOperation("缓存机构名称Redis")
    @RequestMapping(value = ServiceApi.Redis.OrgRedis, method = RequestMethod.POST)
    @ResponseBody
    public String cacheOrgName() throws Exception
    {
        redisInitService.cacheOrgName();
        return "缓存机构名称Redis完成！";
    }

    @ApiOperation("缓存机构区域Redis")
    @RequestMapping(value = ServiceApi.Redis.OrgAreaRedis, method = RequestMethod.POST)
    @ResponseBody
    public String cacheOrgArea() throws Exception
    {
        redisInitService.cacheOrgArea();
        return "缓存机构区域Redis完成！";
    }
}
