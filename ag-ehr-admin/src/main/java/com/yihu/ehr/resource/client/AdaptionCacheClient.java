package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface AdaptionCacheClient {

    @RequestMapping(value = ServiceApi.Adaptions.Cache, method = RequestMethod.POST)
    @ApiOperation("缓存适配数据")
    boolean cacheData(
            @PathVariable("id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.CacheGet, method = RequestMethod.GET)
    @ApiOperation("获取适配数据")
    String getCache(
            @PathVariable(value = "key") String key);

}
