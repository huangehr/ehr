package com.yihu.ehr.redis.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.redis.service.HpIcd10Cache;
import com.yihu.ehr.resource.client.AdapterDictionaryClient;
import com.yihu.ehr.resource.client.AdapterMetadataClient;
import com.yihu.ehr.resource.client.AdaptionCacheClient;
import com.yihu.ehr.std.service.CDAClient;
import com.yihu.ehr.std.service.CDAVersionClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by shine on 2016/7/11.
 */
@RequestMapping(ApiVersion.Version1_0 + "/cache")
@RestController
@Api(value = "cache", description = "数据缓存")
public class RedisController {
    @Autowired
    AdaptionCacheClient adaptionCacheClient;
    @Autowired
    CDAClient cdaClient;
    @Autowired
    HpIcd10Cache hpIcd10Cache;
    @Autowired
    AdapterMetadataClient adapterMetadataClient;
    @Autowired
    OrganizationClient organizationClient;
    @Autowired
    AdapterDictionaryClient adapterDictionaryClient;

    @Autowired
    CDAVersionClient cdaVersionClient;

    @RequestMapping(value= ServiceApi.Adaptions.Cache,method = RequestMethod.POST)
    @ApiOperation("缓存适配数据元")
    public boolean cacheData(
            @ApiParam(name="id",value="schema_id",defaultValue = "")
            @PathVariable(value = "id")String id){
         adaptionCacheClient.cacheData(id);
        return true;
    }

    @ApiOperation("缓存CDA标准数据至Redis")
    @RequestMapping(value = ServiceApi.Caches.Versions, method = RequestMethod.PUT)
    public boolean versions(@ApiParam(value = "版本列表，使用逗号分隔", defaultValue = "000000000000,568ce002559f")
                         @RequestParam("versions") String versions,
                         @ApiParam(value = "强制清除再缓存", defaultValue = "true")
                         @RequestParam("force") boolean force) {
        for (String version : versions.split(",")) {
            cdaClient.versions(version, force);
        }
        return true;
    }

    /**
     * 缓存所有
     * @param force
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/all", method = RequestMethod.POST)
    @ApiOperation(value = "健康问题和ICD10缓存/缓存所有")
    public boolean cacheAll(@ApiParam(name = "force", value = "force")
                                @RequestParam(value = "force") boolean force) {
        hpIcd10Cache.cacheAll(force);
        return true;
    }


    @RequestMapping(value = "/dict/indicators/CacheIndicatorsDict" , method = RequestMethod.GET)
    @ApiOperation(value = "缓存健康问题字典/redis缓存")
    public boolean CacheHpDictByCodes(){

        hpIcd10Cache.CacheHpDictByCodes();
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.MetadataCache,method = RequestMethod.POST)
    @ApiOperation("缓存数据元字典数据")
    public boolean metadataCache()
    {
        adapterMetadataClient.metadataCache();
        return true;
    }


    @ApiOperation("缓存机构数据")
    @RequestMapping(value = ServiceApi.Caches.Organizations, method = RequestMethod.PUT)
    public boolean cache(@ApiParam(value = "reload", defaultValue = "true")
                      @RequestParam("reload") boolean reload) {
        organizationClient.cache(reload);
        return true;
    }

    @ApiOperation("缓存机构区域代码")
    @RequestMapping(value = ServiceApi.Caches.OrganizationsArea, method = RequestMethod.PUT)
    public boolean CacheAreaCode(@ApiParam(value = "reload", defaultValue = "true")
                              @RequestParam("reload") boolean reload) {
        organizationClient.CacheAreaCode(reload);
        return true;
    }

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionariesCache, method = RequestMethod.GET)
    @ApiOperation("缓存适配版本字典数据")
    public boolean dictCache(
            @ApiParam(name = "schemaId", value = "schemaId", defaultValue = "")
            @PathVariable(value = "schemaId") String schemaId)
    {
        adapterDictionaryClient.dictCache(schemaId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Standards.VersionCache, method = RequestMethod.PUT)
    @ApiOperation(value = "向Redis中缓存标准数据")
    public boolean cache(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) {
             cdaVersionClient.cache(version);
        return true;
    }
}
