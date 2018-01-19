package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.redis.client.RedisAppClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by janseny on 2017/11/20.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "Redis", description = "Redis服务", tags = {"Redis服务-App前端-Redis缓存服务"})
public class RedisAppController {

    @Autowired
    private RedisAppClient redisAppClient;

    @ApiOperation("获取单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppGetRedisValue, method = RequestMethod.GET)
    @ResponseBody
    public String getRedisValue(@ApiParam(value = "key", defaultValue = "")
                                 @RequestParam("key") String key ) {
        String result =  redisAppClient.getRedisValue(key);
        return result;
    }

    @ApiOperation("保存单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppSetRedisValue, method = RequestMethod.GET)
    @ResponseBody
    public Boolean setRedisValue(@ApiParam(value = "key", defaultValue = "")
                                @RequestParam("key") String key,
                                @ApiParam(value = "value", defaultValue = "")
                                @RequestParam("value") String value) {
        return redisAppClient.setRedisValue(key,value);
    }


    @ApiOperation("保存单条缓存json字符串")
    @RequestMapping(value = ServiceApi.Redis.AppSetRedisJsonValue, method = RequestMethod.GET)
    @ResponseBody
    public Boolean setRedisJsonValue(@ApiParam(value = "key", defaultValue = "")
                                 @RequestParam("key") String key,
                                 @ApiParam(value = "value", defaultValue = "")
                                 @RequestParam("value") String value) {
        return redisAppClient.setRedisJsonValue(key,value);
    }

    @ApiOperation("删除单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppDeleteRedisValue, method = RequestMethod.GET)
    @ResponseBody
    public Boolean deleteRedisValue(@ApiParam(value = "key", defaultValue = "")
                                 @RequestParam("key") String key) {
        return redisAppClient.deleteRedisValue(key);
    }

}
