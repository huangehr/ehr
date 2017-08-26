package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.resource.model.RsResourcesQuery;
import com.yihu.ehr.resource.service.ResourcesDefaultQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sxy on 2017/08.
 */

@RestController
@RequestMapping(value= ApiVersion.Version1_0)
@Api(value = "ResourceDefaultQuery", description = "资源默认查询条件")
public class RsResourceDefaultQueryEndPoint {

    @Autowired
    private ResourcesDefaultQueryService resourcesDefaultQueryService;

    @RequestMapping(value = ServiceApi.Resources.QueryById, method = RequestMethod.GET)
    @ApiOperation("根据资源id获取默认查询条件值")
    public String getResourceDefaultQueryById(
            @ApiParam(name = "id", value = "资源id")
            @PathVariable(value = "id") String id){
        RsResourcesQuery resourcesQuery = resourcesDefaultQueryService.findByResourcesId(id);
        if(resourcesQuery == null){
            return null;
        }
        return resourcesQuery.getQuery();
    }

}
