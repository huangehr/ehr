package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.service.intf.IResourceMetadataService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
@RestController
@Api(value="ResourceMetadata",description = "资源数据元")
@RequestMapping(value= ApiVersion.Version1_0 + "/resourceMetadata")
public class ResourceMetadataController extends BaseRestController {

    @Autowired
    private IResourceMetadataService rsMetadataService;

    @ApiOperation("创建资源数据元")
    @RequestMapping(method = RequestMethod.POST)
    public RsResourceMetadata createResourceMetadata(
            @ApiParam(name="metadata",value="资源数据元",defaultValue = "")
            @RequestParam(name="metadata")String metadata) throws Exception
    {
        RsResourceMetadata rsMetadata = toEntity(metadata,RsResourceMetadata.class);
        rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
        rsMetadataService.createResourceMetadata(rsMetadata);
        return rsMetadata;
    }

    @ApiOperation("更新资源数据元")
    @RequestMapping(method = RequestMethod.PUT)
    public RsResourceMetadata updateResourceMetadata(
            @ApiParam(name="dimension",value="资源数据元",defaultValue="")
            @RequestParam(name="dimension")String metadata) throws Exception
    {
        RsResourceMetadata  rsMetadata= toEntity(metadata,RsResourceMetadata.class);
        rsMetadataService.updateResourceMetadata(rsMetadata);
        return rsMetadata;
    }

    @ApiOperation("资源数据元删除")
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    public boolean deleteResourceMetadata(
            @ApiParam(name="id",value="资源数据元ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        rsMetadataService.deleteResourceMetadata(id);
        return true;
    }

    @ApiOperation("资源数据元查询")
    @RequestMapping(value="",method = RequestMethod.GET)
    public Collection<RsResourceMetadata> queryDimensions(
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
        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsResourceMetadata> dimensions = rsMetadataService.getResourceMetadata(sorts,reducePage(page),size);
            pagedResponse(request,response,dimensions.getTotalElements(),page,size);
            return convertToModels(dimensions.getContent(),new ArrayList<>(dimensions.getNumber()),RsResourceMetadata.class,fields);
        }
        else
        {
            List<RsResourceMetadata> dimensions = rsMetadataService.search(fields,filters,sorts,page,size);
            pagedResponse(request,response,rsMetadataService.getCount(filters),page,size);
            return convertToModels(dimensions,new ArrayList<>(dimensions.size()),RsResourceMetadata.class,fields);
        }
    }
}
