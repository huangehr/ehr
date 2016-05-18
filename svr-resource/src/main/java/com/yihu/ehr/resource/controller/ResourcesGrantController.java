package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsAppResourceMetadata;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.resource.service.intf.IResourceGrantService;
import com.yihu.ehr.resource.service.intf.IResourceMetadataGrantService;
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
 * Created by lyr on 2016/4/26.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "resourceGrant", description = "资源授权服务接口")
public class ResourcesGrantController extends BaseRestController{
    @Autowired
    private IResourceGrantService rsGrantService;
    @Autowired
    private IResourceMetadataGrantService rsMetadataGrantService;

    @ApiOperation("资源授权单个应用")
    @RequestMapping(value= ServiceApi.Resources.ResourceGrantApp ,method = RequestMethod.POST)
    public MRsAppResource grantResource(
            @ApiParam(name="resource_id",value="资源ID",defaultValue = "")
            @PathVariable(value="resource_id") String resource_id,
            @ApiParam(name="app_id",value="资源ID",defaultValue = "")
            @PathVariable(value="app_id") String app_id) throws Exception
    {
        RsAppResource appRs = new RsAppResource();

        appRs.setId(getObjectId(BizObject.AppResource));
        appRs.setAppId(app_id);
        appRs.setResourceId(resource_id);

        rsGrantService.grantResource(appRs);
        return convertToModel(appRs, MRsAppResource.class);
    }

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value= ServiceApi.Resources.ResourceGrantApps,method = RequestMethod.POST)
    public Collection<MRsAppResource> grantResourceMany(
            @ApiParam(name="resource_id",value="资源ID",defaultValue = "")
            @PathVariable(value="resource_id") String resource_id,
            @ApiParam(name="app_id",value="资源ID",defaultValue = "")
            @RequestParam(name="app_id") String app_id) throws Exception
    {
        String[] appIdArray = app_id.split(",");
        List<RsAppResource> appRsList = new ArrayList<RsAppResource>();

        for(String _appId : appIdArray)
        {
            RsAppResource appRs = new RsAppResource();

            appRs.setId(getObjectId(BizObject.AppResource));
            appRs.setAppId(_appId);
            appRs.setResourceId(resource_id);

            appRsList.add(appRs);
        }

        rsGrantService.grantResourceBatch(appRsList);
        return convertToModels(appRsList,new ArrayList<>(appRsList.size()),MRsAppResource.class,"");
    }

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant,method = RequestMethod.DELETE)
    public boolean deleteGrant(
            @ApiParam(name="id",value="授权ID",defaultValue = "")
            @PathVariable(value="id")String id) throws Exception
    {
        rsGrantService.deleteResourceGrant(id);
        return true;
    }

    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants,method = RequestMethod.DELETE)
    public boolean deleteGrantBatch(
            @ApiParam(name="id",value="授权ID",defaultValue = "")
            @RequestParam(value="id")String id) throws Exception
    {
        rsGrantService.deleteResourceGrant(id);

        return true;
    }

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants,method = RequestMethod.GET)
    public Page<MRsAppResource> queryAppResourceGrant(
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
        Collection<MRsAppResource> rsAppList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsAppResource> rsGrant = rsGrantService.getAppResourceGrant(sorts,reducePage(page),size);
            total = rsGrant.getTotalElements();
            rsAppList = convertToModels(rsGrant.getContent(),new ArrayList<>(rsGrant.getNumber()),MRsAppResource.class,fields);
        }
        else
        {
            List<RsAppResource> rsGrant = rsGrantService.search(fields,filters,sorts,page,size);
            total = rsGrantService.getCount(filters);
            rsAppList = convertToModels(rsGrant,new ArrayList<>(rsGrant.size()),MRsAppResource.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsAppResource> rsAppPage = new PageImpl<MRsAppResource>((List<MRsAppResource>)rsAppList,pageable,total);

        return rsAppPage;
    }

    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp,method = RequestMethod.POST)
    public MRsAppResourceMetadata grantRsMetaData(
            @ApiParam(name="metadata_id",value="资源ID",defaultValue = "")
            @PathVariable(value="metadata_id")String metadata_id,
            @ApiParam(name="app_resource_id",value="资源数据元ID",defaultValue = "")
            @PathVariable(value="app_resource_id")String app_resource_id)throws Exception
    {
        RsAppResourceMetadata appRsMetadata = new RsAppResourceMetadata();
        appRsMetadata.setId(getObjectId(BizObject.AppResourceMetadata));
        appRsMetadata.setAppResourceId(app_resource_id);
        appRsMetadata.setMetadataId(metadata_id);

        rsMetadataGrantService.grantRsMetadata(appRsMetadata);
        return convertToModel(appRsMetadata, MRsAppResourceMetadata.class);
    }

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value=ServiceApi.Resources.ResourceMetadatasGrantApp,method = RequestMethod.POST)
    public Collection<MRsAppResourceMetadata> grantRsMetaDataBatch(
            @ApiParam(name="app_resource_id",value="资源ID",defaultValue = "")
            @PathVariable(value="app_resource_id")String app_resource_id,
            @ApiParam(name="metadata_id",value="资源数据元ID",defaultValue = "")
            @RequestParam(value="metadata_id")String metadata_id)throws Exception
    {
        String[] metadataIdArray = metadata_id.split(",");
        List<RsAppResourceMetadata> appRsMetadataList = new ArrayList<RsAppResourceMetadata>();

        for(String _metadataId : metadataIdArray)
        {
            RsAppResourceMetadata appRsMetadata = new RsAppResourceMetadata();

            appRsMetadata.setId(getObjectId(BizObject.AppResourceMetadata));
            appRsMetadata.setAppResourceId(app_resource_id);
            appRsMetadata.setMetadataId(_metadataId);

            appRsMetadataList.add(appRsMetadata);
        }

        rsMetadataGrantService.grantRsMetadataBatch(appRsMetadataList);
        return convertToModels(appRsMetadataList,new ArrayList<>(appRsMetadataList.size()),MRsAppResourceMetadata.class,"");
    }

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrant,method = RequestMethod.DELETE)
    public boolean deleteMetadataGrant(
            @ApiParam(name="id",value="授权ID",defaultValue = "")
            @PathVariable(value="id")String id) throws Exception
    {
        rsMetadataGrantService.deleteRsMetadataGrant(id);
        return true;
    }

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants,method = RequestMethod.DELETE)
    public boolean deleteMetadataGrantBatch(
            @ApiParam(name="id",value="授权ID",defaultValue = "")
            @RequestParam(value="id")String id) throws Exception
    {
        rsMetadataGrantService.deleteRsMetadataGrant(id);

        return true;
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants,method = RequestMethod.GET)
    public Page<MRsAppResourceMetadata> queryAppRsMetadataGrant(
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
        Collection<MRsAppResourceMetadata> rsAppMetaList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsAppResourceMetadata> rsMetadataGrant = rsMetadataGrantService.getAppRsMetadataGrant(sorts,reducePage(page),size);
            total = rsMetadataGrant.getTotalElements();
            rsAppMetaList = convertToModels(rsMetadataGrant.getContent(),new ArrayList<>(rsMetadataGrant.getNumber()),MRsAppResourceMetadata.class,fields);
        }
        else
        {
            List<RsAppResourceMetadata> rsMetadataGrant = rsMetadataGrantService.search(fields,filters,sorts,page,size);
            total = rsMetadataGrantService.getCount(filters);
            rsAppMetaList =convertToModels(rsMetadataGrant,new ArrayList<>(rsMetadataGrant.size()),MRsAppResourceMetadata.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsAppResourceMetadata> rsPage = new PageImpl<MRsAppResourceMetadata>((List<MRsAppResourceMetadata>)rsAppMetaList,pageable,total);

        return rsPage;
    }
}
