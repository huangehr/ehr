package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.resource.model.RsResourceDefaultQuery;
import com.yihu.ehr.resource.service.RsResourceDefaultQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sxy on 2017/08.
 */

@RestController
@RequestMapping(value= ApiVersion.Version1_0)
@Api(value = "RsResourceDefaultQuery", description = "资源默认查询条件")
public class RsResourceDefaultQueryEndPoint {

    @Autowired
    private RsResourceDefaultQueryService resourcesDefaultQueryService;

    @RequestMapping(value = ServiceApi.Resources.QueryByResourceId, method = RequestMethod.GET)
    @ApiOperation("根据资源id获取默认查询条件值")
    public String getByResourceId(
            @ApiParam(name = "resourceId", value = "资源id")
            @RequestParam(value = "resourceId") String resourceId){
        RsResourceDefaultQuery resourcesQuery = resourcesDefaultQueryService.findByResourcesId(resourceId);
        if(resourcesQuery == null){
            return "{}";
        }
        return resourcesQuery.getQuery();
    }

}
