package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by progr1mmer on 2017/9/11.
 */
@FeignClient(value = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisInitClient {

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterMeta, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据元数据")
    String cacheAdapterMetadata(
            @PathVariable(value = "id") String id);

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterDict, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据字典数据")
    String cacheAdapterDict(
            @PathVariable(value = "id") String id);

    @RequestMapping(value= ServiceApi.Redis.InitRsMetadata, method = RequestMethod.POST)
    @ApiOperation("Redis缓存数据元字典（dict_code不为空）")
    String cacheMetadata();
}
