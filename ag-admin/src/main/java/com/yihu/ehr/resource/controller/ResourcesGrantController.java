package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsAppResourceMetadata;
import com.yihu.ehr.resource.client.ResourcesGrantClient;
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
@Api(value = "resourceGrant", description = "资源授权服务接口")
public class ResourcesGrantController extends BaseController {

    @Autowired
    private ResourcesGrantClient resourcesGrantClient;

    @ApiOperation("单个应用授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResources, method = RequestMethod.POST)
    public Collection<MRsAppResource> grantAppResource(
            @ApiParam(name = "app_id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "app_id") String app_id,
            @ApiParam(name = "resource_id", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resource_id") String resource_id) throws Exception {
        return resourcesGrantClient.grantAppResource(app_id,resource_id);
    }

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantApps, method = RequestMethod.POST)
    public Collection<MRsAppResource> grantResourceApp(
            @ApiParam(name = "resource_id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "resource_id") String resource_id,
            @ApiParam(name = "app_id", value = "资源ID", defaultValue = "")
            @RequestParam(value = "app_id") String app_id) throws Exception {
        return resourcesGrantClient.grantAppResource(app_id,resource_id);
    }

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.DELETE)
    public boolean deleteGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return resourcesGrantClient.deleteGrant(id);
    }

    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.DELETE)
    public boolean deleteGrantBatch(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception {
        return resourcesGrantClient.deleteGrantBatch(id);
    }

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.GET)
    public Page<MRsAppResource> queryAppResourceGrant(
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
        return resourcesGrantClient.queryAppResourceGrant(fields,filters,sorts,page,size);
    }

    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp, method = RequestMethod.POST)
    public MRsAppResourceMetadata grantRsMetaData(
            @ApiParam(name = "metadata_id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "metadata_id") String metadata_id,
            @ApiParam(name = "app_resource_id", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "app_resource_id") String app_resource_id) throws Exception {
        return resourcesGrantClient.grantRsMetaData(metadata_id,app_resource_id);
    }

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrantApp, method = RequestMethod.POST)
    public Collection<MRsAppResourceMetadata> grantRsMetaDataBatch(
            @ApiParam(name = "app_resource_id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "app_resource_id") String app_resource_id,
            @ApiParam(name = "metadata_id", value = "资源数据元ID", defaultValue = "")
            @RequestParam(value = "metadata_id") String metadata_id) throws Exception {
        return resourcesGrantClient.grantRsMetaDataBatch(metadata_id,app_resource_id);
    }

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrant, method = RequestMethod.DELETE)
    public boolean deleteMetadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return resourcesGrantClient.deleteMetadataGrant(id);
    }

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants, method = RequestMethod.DELETE)
    public boolean deleteMetadataGrantBatch(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception {
        return resourcesGrantClient.deleteMetadataGrantBatch(id);
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants, method = RequestMethod.GET)
    public Page<MRsAppResourceMetadata> queryAppRsMetadataGrant(
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
        return resourcesGrantClient.queryAppRsMetadataGrant(fields,filters,sorts,page,size);
    }
}
