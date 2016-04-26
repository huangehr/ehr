package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.yihu.ehr.util.controller.BaseRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/resources")
@Api(value = "resources", description = "资源服务接口")
public class ResourcesController extends BaseRestController {
    @Autowired
    private IResourcesService rsService;

    @ApiOperation("创建资源")
    @RequestMapping(method = RequestMethod.POST)
    public RsResources createResource(
            @ApiParam(name="resource",value="资源",defaultValue = "")
            @RequestParam(name="resource")String resource) throws Exception
    {
        RsResources  rs= toEntity(resource,RsResources.class);
        rs.setId(getObjectId(BizObject.Resources));
        rsService.createResource(rs);
        return rs;
    }

    @ApiOperation("更新资源")
    @RequestMapping(method = RequestMethod.PUT)
    public RsResources updateResources(
            @ApiParam(name="resource",value="资源",defaultValue="")
            @RequestParam(name="resource")String resource) throws Exception
    {
        RsResources  rs= toEntity(resource,RsResources.class);
        rsService.updateResource(rs);
        return rs;
    }

    @ApiOperation("资源删除")
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    public boolean deleteResources(
            @ApiParam(name="id",value="资源ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        rsService.deleteResource(id);
        return true;
    }

    @ApiOperation("资源查询")
    @RequestMapping(method = RequestMethod.GET)
    public Collection<RsResources> queryResources(
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
            Page<RsResources> resources = rsService.getResources(sorts,reducePage(page),size);
            pagedResponse(request,response,resources.getTotalElements(),page,size);
            return convertToModels(resources.getContent(),new ArrayList<>(resources.getNumber()),RsResources.class,fields);
        }
        else
        {
            List<RsResources> resources = rsService.search(fields,filters,sorts,page,size);
            pagedResponse(request,response,rsService.getCount(filters),page,size);
            return convertToModels(resources,new ArrayList<>(resources.size()),RsResources.class,fields);
        }

    }
}