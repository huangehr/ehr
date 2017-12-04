package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.redis.client.RedisCacheStatisticsClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存统计 Controller
 *
 * @author 张进军
 * @date 2017/11/30 17:07
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "缓存统计接口", tags = {"缓存服务管理--缓存统计接口"})
public class RedisCacheStatisticsController extends BaseController {

    @Autowired
    private RedisCacheStatisticsClient redisCacheStatisticsClient;

    @ApiOperation("统计缓存分类的缓存个数")
    @RequestMapping(value = ServiceApi.Redis.CacheStatistics.GetCategoryKeys, method = RequestMethod.GET)
    public Envelop getCategoryKeys() {
        return redisCacheStatisticsClient.getCategoryKeys();
    }

}
