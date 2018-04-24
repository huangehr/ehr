package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.redis.client.RedisInitClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by progr1mmer on 2017/9/11.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "RedisInit", description = "Redis初始化服务", tags = {"Redis服务-Redis初始化服务"})
public class RedisInitController {

    @Autowired
    private RedisInitClient redisInitClient;

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterMeta, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据元数据")
    public String cacheAdapterMetadata(
            @ApiParam(name="id",value="rs_adapter_schema.id")
            @PathVariable(value = "id") String id) throws Exception {
        return redisInitClient.cacheAdapterMetadata(id);
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterDict, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据字典数据")
    public String cacheAdapterDict(
            @ApiParam(name="id",value="rs_adapter_schema.id")
            @PathVariable(value = "id") String id) throws Exception {
        return redisInitClient.cacheAdapterDict(id);
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsMetadata, method = RequestMethod.POST)
    @ApiOperation("Redis缓存数据元字典（dict_code不为空）")
    public String cacheMetadata() throws Exception {
        return redisInitClient.cacheMetadata();
    }

}
