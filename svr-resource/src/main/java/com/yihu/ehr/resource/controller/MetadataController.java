package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.service.intf.IMetadataService;
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
 * 数据元服务接口
 *
 * Created by lyr on 2016/5/16.
 */
@RestController
@RequestMapping(value= ApiVersion.Version1_0 + "/metadata")
@Api(value = "metadata", description = "数据元服务接口")
public class MetadataController  extends BaseRestController{

    @Autowired
    private IMetadataService metadataService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("创建数据元")
    public MRsMetadata createMetadata(
        @ApiParam(name="metadata",value="数据元JSON",defaultValue = "")
        @RequestParam(name="metadata") String metadata) throws Exception
    {
        RsMetadata rsMetadata = toEntity(metadata,RsMetadata.class);
        rsMetadata.setId(getObjectId(BizObject.RsMetadata));
        rsMetadata = metadataService.saveMetadata(rsMetadata);
        return convertToModel(rsMetadata,MRsMetadata.class);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation("更新数据元")
    public MRsMetadata updateMetadata(
            @ApiParam(name="metadata",value="数据元JSON",defaultValue = "")
            @RequestParam(name="metadata") String metadata) throws Exception
    {
        RsMetadata rsMetadata = toEntity(metadata,RsMetadata.class);
        rsMetadata = metadataService.saveMetadata(rsMetadata);
        return convertToModel(rsMetadata,MRsMetadata.class);
    }

    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteMetadata(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @PathVariable(value="id")String id) throws Exception
    {
        metadataService.deleteMetadata(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteMetadataBatch(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @RequestParam(name="id") String id) throws Exception
    {
        metadataService.deleteMetadata(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("查询数据元")
    public Page<MRsMetadata> getMetadata(
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
        Collection<MRsMetadata> metaList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsMetadata> metadataPage = metadataService.getMetadata(sorts,reducePage(page),size);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(),new ArrayList<>(metadataPage.getNumber()),MRsMetadata.class,fields);
        }
        else
        {
            List<RsMetadata> metadata = metadataService.search(fields,filters,sorts,page,size);
            total = metadataService.getCount(filters);
            metaList = convertToModels(metadata,new ArrayList<>(metadata.size()),MRsMetadata.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsMetadata> metaPage = new PageImpl<MRsMetadata>((List<MRsMetadata>)metaList,pageable,total);

        return metaPage;
    }
}
