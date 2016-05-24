package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.resource.client.ResourcesClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resources", description = "资源服务接口")
public class ResourcesController extends BaseController {
    @Autowired
    private ResourcesClient resourcesClient;

    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.POST)
    public MRsResources createResource(
            @ApiParam(name = "resource", value = "资源", defaultValue = "")
            @RequestParam(value = "resource") String resource) throws Exception {
        return resourcesClient.createResource(resource);
    }

    @ApiOperation("更新资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.PUT)
    public MRsResources updateResources(
            @ApiParam(name = "resource", value = "资源", defaultValue = "")
            @RequestParam(value = "resource") String resource) throws Exception {
        return resourcesClient.updateResources(resource);
    }

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.DELETE)
    public boolean deleteResources(
            @ApiParam(name = "id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return resourcesClient.deleteResources(id);
    }

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.DELETE)
    public boolean deleteResourcesPatch(
            @ApiParam(name = "ids", value = "资源ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        return resourcesClient.deleteResourcesPatch(ids);
    }

    @ApiOperation("资源查询")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.GET)
    public Page<MRsResources> queryResources(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        return resourcesClient.queryResources(fields,filters,sorts,page,size);
    }
}