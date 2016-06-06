package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.model.resource.MRsInterface;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.resource.client.ResourcesCategoryClient;
import com.yihu.ehr.resource.client.ResourcesClient;
import com.yihu.ehr.resource.client.RsInterfaceClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resources", description = "资源服务接口")
public class ResourcesController extends BaseController {
    @Autowired
    private ResourcesClient resourcesClient;
    @Autowired
    private RsInterfaceClient rsInterfaceClient;
    @Autowired
    private ResourcesCategoryClient rsCategoryClient;


    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.POST)
    public Envelop createResource(
            @ApiParam(name = "resource", value = "资源", defaultValue = "")
            @RequestParam(value = "resource") String resource) throws Exception {
        Envelop envelop = new Envelop();
        try{
            RsResourcesModel rsResourcesModel = objectMapper.readValue(resource,RsResourcesModel.class);
            MRsResources mRsResources = convertToMModel(rsResourcesModel,MRsResources.class);
            MRsResources rsResources = resourcesClient.createResource(objectMapper.writeValueAsString(mRsResources));
            envelop.setObj(rsResources);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("更新资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.PUT)
    public Envelop updateResources(
            @ApiParam(name = "resource", value = "资源", defaultValue = "")
            @RequestParam(value = "resource") String resource) throws Exception {
        Envelop envelop = new Envelop();
        try{
            RsResourcesModel rsResourcesModel = objectMapper.readValue(resource,RsResourcesModel.class);
            MRsResources mRsResources = convertToMModel(rsResourcesModel,MRsResources.class);
            MRsResources rsResources = resourcesClient.updateResources(objectMapper.writeValueAsString(mRsResources));
            envelop.setObj(rsResources);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.DELETE)
    public Envelop deleteResources(
            @ApiParam(name = "id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesClient.deleteResources(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("批量资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.DELETE)
    public Envelop deleteResourcesBatch(
            @ApiParam(name = "ids", value = "资源ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesClient.deleteResourcesBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Resource,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源")
    public Envelop getResourceById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsResources rsResources = resourcesClient.getResourceById(id);
            envelop.setObj(rsResources);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源查询")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.GET)
    public Envelop queryResources(
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
        try
        {
            ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources(fields,filters,sorts,page,size);
            List<MRsResources> mRsResources = responseEntity.getBody();
            List<RsResourcesModel> rsResources = new ArrayList<>(mRsResources.size());
            for(MRsResources m:mRsResources){
                RsResourcesModel rsResourcesModel = convertToModel(m,RsResourcesModel.class);
                String categoryId =  rsResourcesModel.getCategoryId();
                if (!StringUtils.isEmpty(categoryId)){
                    MRsCategory category = rsCategoryClient.getRsCategoryById(categoryId);
                    rsResourcesModel.setCategoryName(category==null?"":category.getName());
                }
                String rsInterfaceCode = m.getRsInterface();
                if (!StringUtils.isEmpty(rsInterfaceCode)){
                    MRsInterface rsInterface = rsInterfaceClient.findByResourceInterface(rsInterfaceCode);
                    rsResourcesModel.setRsInterfaceName(rsInterface==null?"":rsInterface.getName());
                }
                rsResources.add(rsResourcesModel);
            }
            Envelop envelop = getResult(rsResources, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }
}