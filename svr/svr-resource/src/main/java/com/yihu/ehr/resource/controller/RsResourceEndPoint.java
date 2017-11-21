package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceCategory;
import com.yihu.ehr.resource.service.RsAppResourceService;
import com.yihu.ehr.resource.service.RsResourceCategoryService;
import com.yihu.ehr.resource.service.RsResourceService;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
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
import java.io.IOException;
import java.util.*;

/**
 * Created by lyr on 2016/4/25.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsResource", description = "资源服务接口")
public class RsResourceEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsResourceService rsResourceService;
    @Autowired
    private RsAppResourceService rsAppResourceService;

    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Envelop createResource(
            @ApiParam(name="resource",value="资源",defaultValue = "")
            @RequestBody String resource) throws Exception {
        Envelop envelop = new Envelop();
        RsResource rs = toEntity(resource, RsResource.class);
        rs.setId(getObjectId(BizObject.Resources));
        RsResource newRsResource =  rsResourceService.saveResource(rs);
        envelop.setSuccessFlg(true);
        envelop.setObj(newRsResource);
        return envelop;
    }

    @ApiOperation("更新资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Envelop updateResources(
            @ApiParam(name="resource",value="资源")
            @RequestBody String resource) {
        Envelop envelop = new Envelop();
        RsResource rs = toEntity(resource,RsResource.class);
        RsResource newRsResource =  rsResourceService.saveResource(rs);
        envelop.setSuccessFlg(true);
        envelop.setObj(newRsResource);
        return envelop;
    }

    @ApiOperation("资源删除")
    @RequestMapping(value=ServiceApi.Resources.Resource, method = RequestMethod.DELETE)
    public boolean deleteResources(
            @ApiParam(name="id",value="资源ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception {
        rsResourceService.deleteResource(id);
        return true;
    }

    @ApiOperation("批量资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.DELETE)
    public boolean deleteResourcesBatch(
            @ApiParam(name="ids",value="资源ID",defaultValue = "")
            @RequestParam(value="ids") String ids) throws Exception {
        rsResourceService.deleteResource(ids);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源")
    public Envelop getResourceById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception {
        Envelop envelop = new Envelop();
        RsResource rsResource = rsResourceService.getResourceById(id);
        if(rsResource != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(rsResource);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关资源");
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceTree, method = RequestMethod.GET)
    @ApiOperation("获取资源列表树")
    public Envelop getResourceTree(
            @ApiParam(name = "dataSource", value = "资源类型")
            @RequestParam(value = "dataSource") Integer dataSource,
            @ApiParam(name = "userResource", value = "授权资源")
            @RequestParam(value = "userResource") String userResource,
            @ApiParam(name = "filters", value = "过滤条件(name)")
            @RequestParam(value = "filters", required = false) String filters) {
        Envelop envelop = new Envelop();
        try{
            List<Map<String, Object>> resultList = rsResourceService.getResourceTree(dataSource, userResource, filters);
            envelop.setSuccessFlg(true);
            envelop.setTotalCount(resultList.size());
            envelop.setDetailModelList(resultList);
            return envelop;
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
    }

    @ApiOperation("资源查询")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.GET)
    public List<MRsResources> queryResources(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(value="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(value="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size,
            @ApiParam(name = "rolesId", value = "角色组Id", defaultValue = "")
            @RequestParam(value = "rolesId", required = false) String rolesId,
            @ApiParam(name = "appId", value = "应用Id", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsResources> rsList;
        //过滤条件为空
        if(StringUtils.isEmpty(filters)) {
            Page<RsResource> resources = rsResourceService.getResources(sorts,reducePage(page),size);
            total = resources.getTotalElements();
            rsList = convertToModels(resources.getContent(),new ArrayList<>(resources.getNumber()),MRsResources.class,fields);
        } else {
            if (!StringUtils.isEmpty(rolesId) && !StringUtils.isEmpty(appId)) {
                String resourceIds = rsAppResourceService.getResourceIdByAppId(appId);
                if (!StringUtils.isEmpty(resourceIds)) {
                    filters += ";id=" + resourceIds + ";" ;
                } else {
                    filters += ";id=0000;" ;
                }
            }
            List<RsResource> resources = rsResourceService.search(fields,filters,sorts,page,size);
            total = rsResourceService.getCount(filters);
            rsList = convertToModels(resources,new ArrayList<>(resources.size()),MRsResources.class,fields);
        }
        pagedResponse(request,response,total,page,size);
        return (List<MRsResources>)rsList;
    }

    @ApiOperation("资源查询_不分页")
    @RequestMapping(value = ServiceApi.Resources.NoPageResources, method = RequestMethod.GET)
    public List<MRsResources> queryNoPageResources(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws Exception {
        Collection<MRsResources> mrsList;
        Collection<MRsResources> rsList = rsResourceService.search(filters);
        mrsList = convertToModels(rsList,new ArrayList<>(rsList.size()),MRsResources.class,"");
        return (List<MRsResources>)mrsList;
    }


}