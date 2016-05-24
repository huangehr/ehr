package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsMetadata;
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
public interface MetadataClient {

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.POST)
    @ApiOperation("创建数据元")
    MRsMetadata createMetadata(
            @RequestParam(value = "metadata") String metadata);

    @RequestMapping(value = ServiceApi.Resources.MetadatasBatch, method = RequestMethod.POST)
    @ApiOperation("批量创建数据元")
    Collection<MRsMetadata> createMetadataPatch(
            @RequestParam(value = "metadatas") String metadatas);

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.PUT)
    @ApiOperation("更新数据元")
    MRsMetadata updateMetadata(
            @RequestParam(value = "metadata") String metadata);

    @RequestMapping(value = ServiceApi.Resources.Metadata, method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    boolean deleteMetadata(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    boolean deleteMetadataBatch(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.GET)
    @ApiOperation("查询数据元")
    Page<MRsMetadata> getMetadata(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

}
