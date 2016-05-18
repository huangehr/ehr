package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.service.intf.IAdapterCacheService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lyr on 2016/5/18.
 */
@RestController
@RequestMapping(value= ApiVersion.Version1_0 + "/adapterCache")
@Api("适配数据缓存")
public class AdapterCacheController extends BaseRestController{

    @Autowired
    private IAdapterCacheService adapterCacheService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("缓存适配数据")
    public boolean cacheData()
    {
        adapterCacheService.cacheData();
        return true;
    }

    @RequestMapping(value="/{key}",method = RequestMethod.GET)
    @ApiOperation("获取适配数据")
    public String getCache(
            @ApiParam(name="key",value="key",defaultValue = "")
            @PathVariable(value="key") String key)
    {
        return adapterCacheService.getCache(key);
    }
}
