package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsAdapterSchema;
import com.yihu.ehr.resource.model.RsAdapterSchema;
import com.yihu.ehr.resource.service.intf.IAdapterSchemaService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 适配方案服务
 *
 * Created by lyr on 2016/5/17.
 */
@RestController
@RequestMapping(value= ApiVersion.Version1_0 + "/adapterSchema")
@Api(value = "adapterSchema", description = "适配方案服务")
public class AdapterSchemaController extends BaseRestController {
    @Autowired
    private IAdapterSchemaService schemaService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("创建数据元")
    public MRsAdapterSchema createSchema(
            @ApiParam(name="adapterSchema",value="数据元JSON",defaultValue = "")
            @RequestParam(name="adapterSchema") String adapterSchema) throws Exception
    {
        RsAdapterSchema schema = toEntity(adapterSchema,RsAdapterSchema.class);
        schema.setId(getObjectId(BizObject.RsAdapterSchema));
        schema = schemaService.saveAdapterSchema(schema);
        return convertToModel(schema,MRsAdapterSchema.class);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation("更新数据元")
    public MRsAdapterSchema updateSchema(
            @ApiParam(name="adapterSchemaa",value="数据元JSON",defaultValue = "")
            @RequestParam(name="adapterSchemaa") String adapterSchemaa) throws Exception
    {
        RsAdapterSchema schema = toEntity(adapterSchemaa,RsAdapterSchema.class);
        schema = schemaService.saveAdapterSchema(schema);
        return convertToModel(schema,MRsAdapterSchema.class);
    }

    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteSchema(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @PathVariable(value="id")String id) throws Exception
    {
        schemaService.deleteAdapterSchema(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteSchemaBatch(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @RequestParam(name="id") String id) throws Exception
    {
        schemaService.deleteAdapterSchema(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("查询数据元")
    public Page<MRsAdapterSchema> getSchema(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(name="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(name="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(name="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(name="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(name="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        Pageable pageable = new PageRequest(reducePage(page),size);
        long total = 0;
        Collection<MRsAdapterSchema> metaList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsAdapterSchema> metadataPage = schemaService.getAdapterSchema(sorts,reducePage(page),size);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(),new ArrayList<>(metadataPage.getNumber()),MRsAdapterSchema.class,fields);
        }
        else
        {
            List<RsAdapterSchema> metadata = schemaService.search(fields,filters,sorts,page,size);
            total = schemaService.getCount(filters);
            metaList = convertToModels(metadata,new ArrayList<>(metadata.size()),MRsAdapterSchema.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsAdapterSchema> metaPage = new PageImpl<MRsAdapterSchema>((List<MRsAdapterSchema>)metaList,pageable,total);

        return metaPage;
    }
}
