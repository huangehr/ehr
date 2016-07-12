package com.yihu.ehr.redis.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by shine on 2016/7/11.
 */

@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Dictionary)
public interface HpIcd10Cache {

    /**
     * 缓存单个
     * @param icd10Id
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/one", method = RequestMethod.POST)
    @ApiOperation(value = "缓存单个")
    void cacheOne(
            @ApiParam(name = "icd10_id", value = "icd10_id")
            @RequestParam(value = "icd10_id") long icd10Id);

    /**
     * 缓存所有
     * @param force
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/all", method = RequestMethod.POST)
    @ApiOperation(value = "缓存所有")
    void cacheAll(boolean force);

    @RequestMapping(value = "/dict/indicators/CacheIndicatorsDict" , method = RequestMethod.GET)
    @ApiOperation(value = "缓存健康问题字典/redis缓存")
    void CacheHpDictByCodes();
}
