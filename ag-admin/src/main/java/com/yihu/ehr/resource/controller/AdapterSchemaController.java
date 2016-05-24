package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAdapterSchema;
import com.yihu.ehr.resource.client.AdapterSchemaClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "adapterSchema", description = "适配方案服务")
public class AdapterSchemaController extends BaseController {

    @Autowired
    private AdapterSchemaClient adapterSchemaClient;

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.POST)
    @ApiOperation("创建适配方案")
    public MRsAdapterSchema createSchema(
            @ApiParam(name = "adapterSchema", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "adapterSchema") String adapterSchema) throws Exception {
       return adapterSchemaClient.createSchema(adapterSchema);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.PUT)
    @ApiOperation("更新适配方案")
    public MRsAdapterSchema updateSchema(
            @ApiParam(name = "adapterSchema", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "adapterSchema") String adapterSchema) throws Exception {
        return adapterSchemaClient.updateSchema(adapterSchema);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schema, method = RequestMethod.DELETE)
    @ApiOperation("删除适配方案")
    public boolean deleteSchema(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return adapterSchemaClient.deleteSchema(id);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配方案")
    public boolean deleteSchemaBatch(
            @ApiParam(name = "ids", value = "数据元ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        return adapterSchemaClient.deleteSchemaBatch(ids);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.GET)
    @ApiOperation("查询适配方案")
    public Page<MRsAdapterSchema> getSchema(
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
        return adapterSchemaClient.getSchema(fields,filters,sorts,page,size);
    }
}
