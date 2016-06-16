package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsMetadata;
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
public interface MetadataClient {

    @RequestMapping(value = ServiceApi.Resources.MetadataList, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    MRsMetadata createMetadata(
            @RequestBody String metadata);

    @RequestMapping(value = ServiceApi.Resources.MetadataBatch, method = RequestMethod.POST)
    @ApiOperation("批量创建数据元")
    boolean createMetadataPatch(
            @RequestParam(value = "metadatas") String metadatas);

    @RequestMapping(value = ServiceApi.Resources.MetadataList, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新数据元")
    MRsMetadata updateMetadata(
            @RequestBody String metadata);

    @RequestMapping(value = ServiceApi.Resources.Metadata, method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    boolean deleteMetadata(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.MetadataList, method = RequestMethod.DELETE)
    @ApiOperation("批量删除数据元")
    boolean deleteMetadataBatch(
            @RequestParam(value = "ids") String id);

    @RequestMapping(value = ServiceApi.Resources.Metadata,method = RequestMethod.GET)
    @ApiOperation("根据ID获取数据元")
    public MRsMetadata getMetadataById(
            @PathVariable(value="id") String id);

    @RequestMapping(value = ServiceApi.Resources.MetadataList, method = RequestMethod.GET)
    @ApiOperation("查询数据元")
    ResponseEntity<List<MRsMetadata>> getMetadata(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Resources.MetadataExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    boolean isExistence(
            @RequestParam(value="filters") String filters);


    @RequestMapping(value = ServiceApi.Resources.MetadataStdCodeExistence,method = RequestMethod.GET)
    @ApiOperation("获取已存在内部编码")
    List stdCodeExistence(
            @RequestParam(value="std_codes") String stdCodes);

    @RequestMapping(value = ServiceApi.Resources.MetadataIdExistence,method = RequestMethod.GET)
    @ApiOperation("获取已存在资源标准编码")
    List idExistence(
            @RequestParam(value="ids") String ids);
}
