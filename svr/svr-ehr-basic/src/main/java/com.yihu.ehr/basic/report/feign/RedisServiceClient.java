package com.yihu.ehr.basic.report.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@FeignClient(value = MicroServices.Redis)
@RequestMapping(ApiVersion.Version1_0)
public interface RedisServiceClient {

    @RequestMapping(value = ServiceApi.Redis.CacheOperation.Get, method = RequestMethod.GET)
    Envelop get(
            @RequestParam("keyRuleCode") String keyRuleCode,
            @RequestParam(value = "ruleParams", required = false) String ruleParams);

    @RequestMapping(value = ServiceApi.Redis.CacheOperation.Set, method = RequestMethod.POST)
    Envelop set(
            @RequestParam("appId") String appId,
            @RequestParam("value") String value,
            @RequestParam("keyRuleCode") String keyRuleCode,
            @RequestParam(value = "ruleParams", required = false) String ruleParams);

    @RequestMapping(value = ServiceApi.Redis.CacheOperation.Remove, method = RequestMethod.POST)
    Envelop remove(
            @RequestParam("keyRuleCode") String keyRuleCode,
            @RequestParam(value = "ruleParams", required = false) String ruleParams);
}
