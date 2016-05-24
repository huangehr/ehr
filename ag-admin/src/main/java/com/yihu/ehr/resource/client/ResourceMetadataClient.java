package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsResourceMetadata;
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
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceMetadataClient {

    @ApiOperation("创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.POST)
    MRsResourceMetadata createResourceMetadata(
            @RequestParam(value = "metadata") String metadata);

    @ApiOperation("批量创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasBatch, method = RequestMethod.POST)
    Collection<MRsResourceMetadata> createResourceMetadataBatch(
            @RequestParam(value = "metadatas") String metadatas);

    @ApiOperation("更新资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.PUT)
    MRsResourceMetadata updateResourceMetadata(
            @RequestParam(value = "dimension") String metadata);

    @ApiOperation("资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata, method = RequestMethod.DELETE)
    boolean deleteResourceMetadata(
            @PathVariable(value = "id") String id);

    @ApiOperation("根据资源ID批量删除资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.DELETE)
    boolean deleteResourceMetadataPatch(
            @RequestParam(value = "resourceId") String resourceId);

    @ApiOperation("资源数据元查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatas, method = RequestMethod.GET)
    Page<MRsResourceMetadata> queryDimensions(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);
}
