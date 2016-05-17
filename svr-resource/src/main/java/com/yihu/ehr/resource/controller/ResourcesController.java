package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesService;
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
    public MRsResources createResource(
            @ApiParam(name="resource",value="资源",defaultValue = "")
            @RequestParam(name="resource")String resource) throws Exception
    {
        RsResources rs = toEntity(resource,RsResources.class);
        rs.setId(getObjectId(BizObject.Resources));
        rsService.saveResource(rs);
        return convertToModel(rs,MRsResources.class);
    }

    @ApiOperation("更新资源")
    @RequestMapping(method = RequestMethod.PUT)
    public MRsResources updateResources(
            @ApiParam(name="resource",value="资源",defaultValue="")
            @RequestParam(name="resource")String resource) throws Exception
    {
        RsResources rs = toEntity(resource,RsResources.class);
        rsService.saveResource(rs);
        return convertToModel(rs,MRsResources.class);
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

    @ApiOperation("资源删除")
    @RequestMapping(method = RequestMethod.DELETE)
    public boolean deleteResourcesPatch(
            @ApiParam(name="id",value="资源ID",defaultValue = "")
            @RequestParam(name="id") String id) throws Exception
    {
        rsService.deleteResource(id);
        return true;
    }

    @ApiOperation("资源查询")
    @RequestMapping(method = RequestMethod.GET)
    public Page<MRsResources> queryResources(
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
        Collection<MRsResources> rsList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsResources> resources = rsService.getResources(sorts,reducePage(page),size);
            total = resources.getTotalElements();
            rsList = convertToModels(resources.getContent(),new ArrayList<>(resources.getNumber()),MRsResources.class,fields);
        }
        else
        {
            List<RsResources> resources = rsService.search(fields,filters,sorts,page,size);
            total = rsService.getCount(filters);
            rsList = convertToModels(resources,new ArrayList<>(resources.size()),MRsResources.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsResources> rsPage = new PageImpl<MRsResources>((List<MRsResources>)rsList,pageable,total);

        return rsPage;
    }
}