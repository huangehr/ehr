package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface AdapterMetadataClient {

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建适配数据元")
    MRsAdapterMetadata createMetadata(
            @RequestBody String adapterMetadata);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新适配数据元")
    MRsAdapterMetadata updateMetadata(
            @RequestBody String adapterMetadata);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.DELETE)
    @ApiOperation("删除适配数据元")
    boolean deleteMetadata(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配数据元")
    boolean deleteMetadataBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata,method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配数据元")
    public MRsAdapterMetadata getMetadataById(
            @PathVariable(value="id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.GET)
    @ApiOperation("查询适配数据元")
    ResponseEntity<List<MRsAdapterMetadata>> getMetadata(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatasBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("批量创建适配数据元")
    MRsAdapterMetadata createRsMetaDataBatch(@RequestBody String jsonData);
}
