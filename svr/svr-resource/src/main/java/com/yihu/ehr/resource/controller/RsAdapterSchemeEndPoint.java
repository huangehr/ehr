package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAdapterSchema;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import com.yihu.ehr.resource.service.RsAdapterSchemeService;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.id.BizObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 适配方案服务
 * <p/>
 * Created by lyr on 2016/5/17.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsAdapterSchemeEndPoint", description = "适配方案", tags = {"资源服务-适配方案"})
public class RsAdapterSchemeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsAdapterSchemeService schemaService;

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建适配方案")
    public MRsAdapterSchema createSchema(
            @ApiParam(name = "adapterSchema", value = "数据元JSON", defaultValue = "")
            @RequestBody String adapterSchema) throws Exception {
        RsAdapterScheme schema = toEntity(adapterSchema, RsAdapterScheme.class);
        schema.setId(getObjectId(BizObject.RsAdapterSchema));
        schema = schemaService.saveAdapterScheme(schema);
        return convertToModel(schema, MRsAdapterSchema.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新适配方案")
    public MRsAdapterSchema updateSchema(
            @ApiParam(name = "adapterScheme", value = "数据元JSON", defaultValue = "")
            @RequestBody String adapterSchema) throws Exception {
        RsAdapterScheme schema = toEntity(adapterSchema, RsAdapterScheme.class);
        schema = schemaService.save(schema);
        return convertToModel(schema, MRsAdapterSchema.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Scheme, method = RequestMethod.DELETE)
    @ApiOperation("删除适配方案")
    public boolean deleteSchema(
            @ApiParam(name = "id", value = "方案id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        schemaService.deleteById(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配方案")
    public boolean deleteSchemaBatch(
            @ApiParam(name = "ids", value = "ids", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        schemaService.deleteAdapterScheme(ids);
        return true;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Scheme, method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配方案")
    public MRsAdapterSchema getAdapterSchemaById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(schemaService.getAdapterSchemeById(id), MRsAdapterSchema.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemes, method = RequestMethod.GET)
    @ApiOperation("查询适配方案")
    public List<MRsAdapterSchema> getSchema(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsAdapterSchema> metaList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsAdapterScheme> metadataPage = schemaService.getAdapterScheme(sorts, reducePage(page), size);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(), new ArrayList<>(metadataPage.getNumber()), MRsAdapterSchema.class, fields);
        } else {
            List<RsAdapterScheme> metadata = schemaService.search(fields, filters, sorts, page, size);
            total = schemaService.getCount(filters);
            metaList = convertToModels(metadata, new ArrayList<>(metadata.size()), MRsAdapterSchema.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsAdapterSchema>) metaList;
    }
}
