package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 缓存统计 Client
 *
 * @author 张进军
 * @date 2017/11/30 17:07
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisCacheStatisticsClient {

    @ApiOperation("统计缓存分类的缓存个数")
    @RequestMapping(value = ServiceApi.Redis.CacheStatistics.GetCategoryKeys, method = RequestMethod.GET)
    public Envelop getCategoryKeys();

}
