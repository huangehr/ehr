package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.client.AdaptionCacheClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "adaption_cache", description = "适配数据缓存")
public class AdaptionCacheController extends BaseController {
    @Autowired
    private AdaptionCacheClient adaptionCacheClient;

    @RequestMapping(value = ServiceApi.Adaptions.Cache, method = RequestMethod.POST)
    @ApiOperation("缓存适配数据")
    public boolean cacheData(
            @ApiParam(name = "schema_id", value = "schema_id", defaultValue = "")
            @PathVariable("schema_id") String schema_id) {
        return adaptionCacheClient.cacheData(schema_id);
    }

    @RequestMapping(value = ServiceApi.Adaptions.CacheGet, method = RequestMethod.GET)
    @ApiOperation("获取适配数据")
    public String getCache(
            @ApiParam(name = "key", value = "key", defaultValue = "")
            @PathVariable(value = "key") String key) {
        return adaptionCacheClient.getCache(key);
    }
}
