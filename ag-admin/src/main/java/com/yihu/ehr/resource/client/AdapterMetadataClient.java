package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.POST)
    @ApiOperation("创建适配数据元")
    MRsAdapterMetadata createMetadata(
            @RequestParam(value = "adapterMetadata") String adapterMetadata);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.PUT)
    @ApiOperation("更新适配数据元")
    MRsAdapterMetadata updateMetadata(
            @RequestParam(value = "adapterMetadata") String adapterMetadata);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.DELETE)
    @ApiOperation("删除适配数据元")
    boolean deleteMetadata(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配数据元")
    boolean deleteMetadataBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.GET)
    @ApiOperation("查询适配数据元")
    ResponseEntity<List<MRsAdapterMetadata>> getMetadata(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

}
