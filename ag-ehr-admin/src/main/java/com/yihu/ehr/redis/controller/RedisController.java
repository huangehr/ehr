package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.redis.client.RedisClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by progr1mmer on 2017/9/11.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "Redis", description = "Redis服务", tags = {"Redis服务-Redis缓存服务"})
public class RedisController {

    @Autowired
    private RedisClient redisClient;

    @ApiOperation("获取资源化数据元映射")
    @RequestMapping(value = ServiceApi.Redis.RsAdapterMetadata, method = RequestMethod.GET)
    @ResponseBody
    public String getRsAdaptionMetaData(@ApiParam(value = "cdaVersion", defaultValue = "")
                                        @RequestParam("cdaVersion") String cdaVersion,
                                        @ApiParam(value = "dictCode", defaultValue = "")
                                        @RequestParam("dictCode") String dictCode,
                                        @ApiParam(value = "srcDictEntryCode", defaultValue = "")
                                        @RequestParam("srcDictEntryCode") String srcDictEntryCode) {
        return redisClient.getRsAdaptionMetaData(cdaVersion,dictCode,srcDictEntryCode);
    }
}
