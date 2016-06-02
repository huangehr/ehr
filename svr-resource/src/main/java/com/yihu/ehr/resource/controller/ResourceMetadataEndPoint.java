package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsResourceMetadata;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.service.ResourceMetadataService;
import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
@RestController
@Api(value = "ResourceMetadata", description = "资源数据元")
@RequestMapping(value = ApiVersion.Version1_0)
public class ResourceMetadataEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceMetadataService rsMetadataService;

    @ApiOperation("创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MRsResourceMetadata createResourceMetadata(
            @ApiParam(name = "metadata", value = "资源数据元", defaultValue = "")
            @RequestBody String metadata) throws Exception {
        RsResourceMetadata rsMetadata = toEntity(metadata, RsResourceMetadata.class);
        rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
        rsMetadataService.saveResourceMetadata(rsMetadata);
        return convertToModel(rsMetadata, MRsResourceMetadata.class);
    }

    @ApiOperation("批量创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Collection<MRsResourceMetadata> createResourceMetadataBatch(
            @ApiParam(name = "metadatas", value = "资源数据元", defaultValue = "")
            @RequestBody String metadatas) throws Exception {
        RsResourceMetadata[] rsMetadata = toEntity(metadatas, RsResourceMetadata[].class);

        for (RsResourceMetadata metadata : rsMetadata) {
            metadata.setId(getObjectId(BizObject.ResourceMetadata));
        }

        List<RsResourceMetadata> metadataList = rsMetadataService.saveMetadataBatch(rsMetadata);

        return convertToModels(metadataList, new ArrayList<MRsResourceMetadata>(), MRsResourceMetadata.class, "");
    }

    @ApiOperation("更新资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.PUT)
    public MRsResourceMetadata updateResourceMetadata(
            @ApiParam(name = "dimension", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "dimension") String metadata) throws Exception {
        RsResourceMetadata rsMetadata = toEntity(metadata, RsResourceMetadata.class);
        rsMetadataService.saveResourceMetadata(rsMetadata);
        return convertToModel(rsMetadata, MRsResourceMetadata.class);
    }

    @ApiOperation("资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata, method = RequestMethod.DELETE)
    public boolean deleteResourceMetadata(
            @ApiParam(name = "id", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        rsMetadataService.deleteResourceMetadata(id);
        return true;
    }

    @ApiOperation("根据ids批量资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatch, method = RequestMethod.DELETE)
    public boolean deleteResourceMetadataBatchById(
            @ApiParam(name = "ids", value = "ids", defaultValue = "")
            @RequestParam(value = "ids") String[] ids) throws Exception {
        rsMetadataService.delete(ids);
        return true;
    }

    @ApiOperation("根据resourceId删除资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.DELETE)
    public boolean deleteResourceMetadataBatchByResourceId(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        rsMetadataService.deleteRsMetadataByResourceId(resourceId);
        return true;
    }

    @ApiOperation("根据ResourceIds列表批量资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatchByResourceId, method = RequestMethod.DELETE)
    public boolean deleteResourceMetadataBatchByResourceId(
            @ApiParam(name = "resource_ids", value = "resource_ids", defaultValue = "")
            @RequestParam(value = "resource_ids") String[] resourceIds) throws Exception {
        rsMetadataService.deleteByResourcesIds(resourceIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元")
    public MRsResourceMetadata getRsMetadataById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rsMetadataService.getRsMetadataById(id), MRsResourceMetadata.class);
    }

    @RequestMapping(value = "/resources/{resources_id}/metadata_list", method = RequestMethod.GET)
    @ApiOperation("根据资源id(resourcesId)获取资源数据元列表")
    public List<MRsResourceMetadata> getRsMetadataByResourcesId(
            @ApiParam(name = "resources_id", value = "resources_id", defaultValue = "")
            @PathVariable(value = "resources_id") String resourcesId) throws Exception {
        List<RsResourceMetadata> metadataList = rsMetadataService.getRsMetadataByResourcesId(resourcesId);
        return (List<MRsResourceMetadata>) convertToModels(metadataList, new ArrayList<MRsResourceMetadata>(metadataList.size()), MRsResourceMetadata.class, "");
    }

    @ApiOperation("资源数据元查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.GET)
    public List<MRsResourceMetadata> queryDimensions(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsResourceMetadata> rsAppMetaList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsResourceMetadata> dimensions = rsMetadataService.getResourceMetadata(sorts, reducePage(page), size);
            total = dimensions.getTotalElements();
            rsAppMetaList = convertToModels(dimensions.getContent(), new ArrayList<>(dimensions.getNumber()), MRsResourceMetadata.class, fields);
        } else {
            List<RsResourceMetadata> dimensions = rsMetadataService.search(fields, filters, sorts, page, size);
            total = rsMetadataService.getCount(filters);
            rsAppMetaList = convertToModels(dimensions, new ArrayList<>(dimensions.size()), MRsResourceMetadata.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsResourceMetadata>) rsAppMetaList;
    }
}
