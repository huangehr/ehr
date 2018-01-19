package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by progr1mmer on 2017/9/11.
 */
@FeignClient(value = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisAppClient {

    @ApiOperation("获取单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppGetRedisValue, method = RequestMethod.GET)
    @ResponseBody
    String getRedisValue(@RequestParam("key") String key);

    @ApiOperation("保存单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppSetRedisValue, method = RequestMethod.GET)
    @ResponseBody
    Boolean setRedisValue(@RequestParam("key") String key,
                         @RequestParam("value") String value);

    @ApiOperation("保存单条缓存Json")
    @RequestMapping(value = ServiceApi.Redis.AppSetRedisJsonValue, method = RequestMethod.POST)
    @ResponseBody
    Boolean setRedisJsonValue(@RequestParam("key") String key,
                              @RequestBody String value);


    @ApiOperation("单条缓存")
    @RequestMapping(value = ServiceApi.Redis.AppDeleteRedisValue, method = RequestMethod.GET)
    @ResponseBody
    Boolean deleteRedisValue(@RequestParam("key") String key);

}
