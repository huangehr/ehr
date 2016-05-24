package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsAppResourceMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourcesGrantClient {

    @ApiOperation("单个应用授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResources, method = RequestMethod.POST)
    Collection<MRsAppResource> grantAppResource(
            @PathVariable(value = "appId") String appId,
            @RequestParam(value = "resourceId") String resourceId);

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantApps, method = RequestMethod.POST)
    Collection<MRsAppResource> grantResourceApp(
            @PathVariable(value = "resourceId") String resourceId,
            @RequestParam(value = "appId") String appId);

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.DELETE)
    boolean deleteGrant(
            @PathVariable(value = "id") String id);
    
    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.DELETE)
    boolean deleteGrantBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Resources.ResourceGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源授权")
    public MRsAppResource getRsAppGrantById(
            @PathVariable(value="id") String id);

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.GET)
    Page<MRsAppResource> queryAppResourceGrant(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);
    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp, method = RequestMethod.POST)
    MRsAppResourceMetadata grantRsMetaData(
            @PathVariable(value = "metadataId") String metadataId,
            @PathVariable(value = "appResourceId") String appResourceId);

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrantApp, method = RequestMethod.POST)
    Collection<MRsAppResourceMetadata> grantRsMetaDataBatch(
            @PathVariable(value = "appResourceId") String appResourceId,
            @RequestParam(value = "metadataIds") String metadataIds);

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrant, method = RequestMethod.DELETE)
    boolean deleteMetadataGrant(
            @PathVariable(value = "id") String id);

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants, method = RequestMethod.DELETE)
    boolean deleteMetadataGrantBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元授权")
    public MRsAppResourceMetadata getRsMetadataGrantById(
            @PathVariable(value="id") String id);

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants, method = RequestMethod.GET)
    Page<MRsAppResourceMetadata> queryAppRsMetadataGrant(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

}
