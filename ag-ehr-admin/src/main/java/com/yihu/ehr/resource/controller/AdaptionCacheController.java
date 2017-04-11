package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.client.AdaptionCacheClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
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
    public Envelop cacheData(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable("id") String id) {
        Envelop envelop = new Envelop();
        try{
            adaptionCacheClient.cacheData(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.CacheGet, method = RequestMethod.GET)
    @ApiOperation("获取适配数据")
    public Envelop getCache(
            @ApiParam(name = "key", value = "key", defaultValue = "")
            @PathVariable(value = "key") String key) {
        Envelop envelop = new Envelop();
        try{
            adaptionCacheClient.getCache(key);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
}
