package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author hzp add at 20170425
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0+"/redis", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "Redis服务", description = "Redis服务")
public class RedisEndPoint extends EnvelopRestEndPoint {

    @Autowired
    RedisService redisService;


    @ApiOperation("获取机构Redis")
    @RequestMapping(value = "orgRedis", method = RequestMethod.GET)
    public String getOrgRedis(
            @ApiParam(name = "key", value = "机构代码", defaultValue = "42017976-4")
            @RequestParam(value = "key") String key) throws Exception {

        return redisService.getOrgRedis(key);
    }

}
