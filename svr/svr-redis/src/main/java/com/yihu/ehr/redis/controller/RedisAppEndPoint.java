package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;


/**
 * @author janseny 2017年11月20日
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "Redis", description = "Redis数据缓存服务-App前端")
public class RedisAppEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisService redisService;

    @ApiOperation("保存单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppSetRedisValue, method = RequestMethod.GET)
    public Boolean setValue(@ApiParam(value = "key", defaultValue = "")
                             @RequestParam("key") String key,
                             @ApiParam(value = "value", defaultValue = "")
                             @RequestParam("value") String value) {
         redisService.setRedisApp(key, value);
         return true;
    }

    @ApiOperation("保存单条缓存Json")
    @RequestMapping(value = ServiceApi.Redis.AppSetRedisJsonValue, method = RequestMethod.POST)
    public Boolean setJsonValue(@ApiParam(value = "key", defaultValue = "")
                            @RequestParam("key") String key,
                            @ApiParam(value = "value", defaultValue = "")
                            @RequestBody String value) {
        redisService.setRedisApp(key, value);
        return true;
    }

    @ApiOperation("获取单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppGetRedisValue, method = RequestMethod.GET)
    public String getValue(@ApiParam(value = "key", defaultValue = "")
                           @RequestParam("key") String key) {
        return redisService.getRedisApp(key);
    }

    @ApiOperation("删除单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppDeleteRedisValue, method = RequestMethod.GET)
    public Boolean deleteValue(@ApiParam(value = "key", defaultValue = "")
                                @RequestParam("key") String key) {
        redisService.deleteRedisApp(key);
        return true;
    }

}
