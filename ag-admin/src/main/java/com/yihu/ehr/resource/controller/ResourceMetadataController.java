package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsResourceMetadata;
import com.yihu.ehr.resource.client.ResourceMetadataClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@Api(value = "ResourceMetadata", description = "资源数据元")
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
public class ResourceMetadataController extends BaseController {

    @Autowired
    private ResourceMetadataClient resourceMetadataClient;

    @ApiOperation("创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.POST)
    public MRsResourceMetadata createResourceMetadata(
            @ApiParam(name = "metadata", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "metadata") String metadata) throws Exception {
        return resourceMetadataClient.createResourceMetadata(metadata);
    }

    @ApiOperation("批量创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasBatch, method = RequestMethod.POST)
    public Collection<MRsResourceMetadata> createResourceMetadataBatch(
            @ApiParam(name = "metadatas", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "metadatas") String metadatas) throws Exception {
        return resourceMetadataClient.createResourceMetadataBatch(metadatas);
    }

    @ApiOperation("更新资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.PUT)
    public MRsResourceMetadata updateResourceMetadata(
            @ApiParam(name = "dimension", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "dimension") String metadata) throws Exception {
        return resourceMetadataClient.updateResourceMetadata(metadata);
    }

    @ApiOperation("资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata, method = RequestMethod.DELETE)
    public boolean deleteResourceMetadata(
            @ApiParam(name = "id", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return resourceMetadataClient.deleteResourceMetadata(id);
    }

    @ApiOperation("根据资源ID批量删除资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.DELETE)
    public boolean deleteResourceMetadataPatch(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        return resourceMetadataClient.deleteResourceMetadataPatch(resourceId);
    }

    @ApiOperation("资源数据元查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.GET)
    public Page<MRsResourceMetadata> queryDimensions(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(name = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(name = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(name = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(name = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(name = "size", required = false) int size) throws Exception {
        return resourceMetadataClient.queryDimensions(fields,filters,sorts,page,size);
    }
}
