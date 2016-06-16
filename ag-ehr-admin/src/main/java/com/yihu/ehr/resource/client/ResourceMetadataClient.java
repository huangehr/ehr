package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsResourceMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceMetadataClient {

    @ApiOperation("创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsResourceMetadata createResourceMetadata(
            @RequestBody String metadata);

    @ApiOperation("批量创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Collection<MRsResourceMetadata> createResourceMetadataBatch(
            @RequestParam(value = "metadatas") String metadatas);

    @ApiOperation("更新资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsResourceMetadata updateResourceMetadata(
            @RequestBody String metadata);

    @ApiOperation("资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata, method = RequestMethod.DELETE)
    boolean deleteResourceMetadata(
            @PathVariable(value = "id") String id);

    @ApiOperation("根据资源ID批量删除资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.DELETE)
    boolean deleteResourceMetadataBatch(
            @RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元")
    MRsResourceMetadata getRsMetadataById(
            @PathVariable(value="id") String id);

    @ApiOperation("资源数据元查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.GET)
    ResponseEntity<List<MRsResourceMetadata>> queryDimensions(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("根据ids批量资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatch, method = RequestMethod.DELETE)
    void deleteResourceMetadataBatchById(
            @RequestParam(value = "ids", required = false) List<String> ids);



    @RequestMapping(value = "/resources/{resources_id}/metadata_list", method = RequestMethod.GET)
    @ApiOperation("根据资源id(resourcesId)获取资源数据元列表")
    List<MRsResourceMetadata> getRsMetadataByResourcesId(
            @PathVariable(value = "resources_id") String resourcesId);

}
