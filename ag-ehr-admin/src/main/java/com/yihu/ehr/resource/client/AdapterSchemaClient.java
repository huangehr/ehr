package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsAdapterSchema;
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
public interface AdapterSchemaClient {

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建适配方案")
    MRsAdapterSchema createSchema(
            @RequestBody String adapterSchema);

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新适配方案")
    MRsAdapterSchema updateSchema(
            @RequestBody String adapterSchema);

    @RequestMapping(value = ServiceApi.Adaptions.Scheme, method = RequestMethod.DELETE)
    @ApiOperation("删除适配方案")
    boolean deleteSchema(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配方案")
    boolean deleteSchemaBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Adaptions.Scheme,method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配方案")
    public MRsAdapterSchema getAdapterSchemaById(
            @PathVariable(value="id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.GET)
    @ApiOperation("查询适配方案")
    ResponseEntity<List<MRsAdapterSchema>> getSchema(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

}
