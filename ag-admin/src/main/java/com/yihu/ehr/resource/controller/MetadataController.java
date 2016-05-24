package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.resource.client.MetadataClient;
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
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "metadata", description = "数据元服务接口")
public class MetadataController extends BaseController {

    @Autowired
    private MetadataClient metadataClient;

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.POST)
    @ApiOperation("创建数据元")
    public MRsMetadata createMetadata(
            @ApiParam(name = "metadata", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "metadata") String metadata) throws Exception {
        return metadataClient.createMetadata(metadata);
    }

    @RequestMapping(value = ServiceApi.Resources.MetadatasBatch, method = RequestMethod.POST)
    @ApiOperation("批量创建数据元")
    public Collection<MRsMetadata> createMetadataPatch(
            @ApiParam(name = "metadatas", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "metadatas") String metadatas) throws Exception {
        return metadataClient.createMetadataPatch(metadatas);
    }

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.PUT)
    @ApiOperation("更新数据元")
    public MRsMetadata updateMetadata(
            @ApiParam(name = "metadata", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "metadata") String metadata) throws Exception {
        return metadataClient.updateMetadata(metadata);
    }

    @RequestMapping(value = ServiceApi.Resources.Metadata, method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteMetadata(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return metadataClient.deleteMetadata(id);
    }

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteMetadataBatch(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception {
        return metadataClient.deleteMetadataBatch(id);
    }

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.GET)
    @ApiOperation("查询数据元")
    public Page<MRsMetadata> getMetadata(
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
        return metadataClient.getMetadata(fields,filters,sorts,page,size);
    }
}
