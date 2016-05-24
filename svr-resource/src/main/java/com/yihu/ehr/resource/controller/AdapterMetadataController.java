package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.service.intf.IAdapterMetadataService;
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
 * 适配数据元服务
 *
 * Created by lyr on 2016/5/17.
 */
@RestController
@RequestMapping(value= ApiVersion.Version1_0)
@Api(value = "adapterMetadata", description = "适配数据元服务")
public class AdapterMetadataController extends BaseRestController {
    @Autowired
    private IAdapterMetadataService metadataService;

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas,method = RequestMethod.POST)
    @ApiOperation("创建适配数据元")
    public MRsAdapterMetadata createMetadata(
            @ApiParam(name="adapterMetadata",value="数据元JSON",defaultValue = "")
            @RequestParam(name="adapterMetadata") String adapterMetadata) throws Exception
    {
        RsAdapterMetadata metadata = toEntity(adapterMetadata,RsAdapterMetadata.class);
        metadata.setId(getObjectId(BizObject.RsAdapterMetadata));
        metadata = metadataService.saveAdapterMetadata(metadata);
        return convertToModel(metadata,MRsAdapterMetadata.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas,method = RequestMethod.PUT)
    @ApiOperation("更新适配数据元")
    public MRsAdapterMetadata updateMetadata(
            @ApiParam(name="adapterMetadata",value="数据元JSON",defaultValue = "")
            @RequestParam(name="adapterMetadata") String adapterMetadata) throws Exception
    {
        RsAdapterMetadata metadata = toEntity(adapterMetadata,RsAdapterMetadata.class);
        metadata = metadataService.saveAdapterMetadata(metadata);
        return convertToModel(metadata,MRsAdapterMetadata.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata,method = RequestMethod.DELETE)
    @ApiOperation("删除适配数据元")
    public boolean deleteMetadata(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @PathVariable(value="id")String id) throws Exception
    {
        metadataService.deleteAdapterMetadata(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas,method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配数据元")
    public boolean deleteMetadataBatch(
            @ApiParam(name="ids",value="数据元ID",defaultValue = "")
            @RequestParam(name="ids") String ids) throws Exception
    {
        metadataService.deleteAdapterMetadata(ids);
        return true;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas,method = RequestMethod.GET)
    @ApiOperation("查询适配数据元")
    public Page<MRsAdapterMetadata> getMetadata(
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
            HttpServletResponse response) throws Exception {
        Pageable pageable = new PageRequest(reducePage(page), size);
        long total = 0;
        Collection<MRsAdapterMetadata> metaList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsAdapterMetadata> metadataPage = metadataService.getAdapterMetadata(sorts, reducePage(page), size);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(), new ArrayList<>(metadataPage.getNumber()), MRsAdapterMetadata.class, fields);
        } else {
            List<RsAdapterMetadata> metadata = metadataService.search(fields, filters, sorts, page, size);
            total = metadataService.getCount(filters);
            metaList = convertToModels(metadata, new ArrayList<>(metadata.size()), MRsAdapterMetadata.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        Page<MRsAdapterMetadata> metaPage = new PageImpl<MRsAdapterMetadata>((List<MRsAdapterMetadata>) metaList, pageable, total);

        return metaPage;
    }
}
