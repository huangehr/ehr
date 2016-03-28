package com.yihu.ehr.org.controller;

import com.netflix.discovery.converters.Auto;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.org.OrgCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 17:00
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "org", description = "组织机构数据缓存服务", tags = {"缓存"})
public class OrgCacheEndPoint {
    @Autowired
    OrgCache orgCache;

    @Auto
    CacheReader cacheReader;

    @ApiOperation("缓存机构数据")
    @RequestMapping(value = RestApi.Caches.Organizations, method = RequestMethod.PUT)
    public void cache(@ApiParam(value = "reload", defaultValue = "true")
                      @RequestParam("reload") boolean reload) {
        orgCache.cacheData(reload);
    }

    @ApiOperation("获取缓存机构列表")
    @RequestMapping(value = RestApi.Caches.Organizations, method = RequestMethod.GET)
    public ResponseEntity<List<MOrganization>> organizations() {
        return new ResponseEntity<>(orgCache.organizations(), HttpStatus.OK);
    }

    @ApiOperation("获取缓存机构")
    @RequestMapping(value = RestApi.Caches.Organization, method = RequestMethod.GET)
    public ResponseEntity<MOrganization> organization(@ApiParam(value = "org_code", defaultValue = "")
                                                      @RequestParam("org_code") String orgCode) {
        return new ResponseEntity<>(orgCache.organization(orgCode), HttpStatus.OK);
    }
}
