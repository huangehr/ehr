package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsAppResourceMetadata;
import com.yihu.ehr.resource.client.ResourcesGrantClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceGrant", description = "资源授权服务接口")
public class ResourcesGrantController extends BaseController {

    @Autowired
    private ResourcesGrantClient resourcesGrantClient;

    @ApiOperation("单个应用授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResources, method = RequestMethod.POST)
    public Envelop grantAppResource(
            @ApiParam(name = "appId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "appId") String appId,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsAppResource> rsAppResource = resourcesGrantClient.grantAppResource(appId,resourceIds);
            envelop.setObj(rsAppResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantApps, method = RequestMethod.POST)
    public Envelop grantResourceApp(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "resourceId") String resourceId,
            @ApiParam(name = "appIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "appIds") String appIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsAppResource> rsAppResource = resourcesGrantClient.grantResourceApp(resourceId,appIds);
            envelop.setObj(rsAppResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.DELETE)
    public Envelop deleteGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteGrant(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.DELETE)
    public Envelop deleteGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteGrantBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源授权")
    public Envelop getRsAppGrantById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAppResource rsAppResource = resourcesGrantClient.getRsAppGrantById(id);
            envelop.setObj(rsAppResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.GET)
    public Envelop queryAppResourceGrant(
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
            ResponseEntity<List<MRsAppResource>> responseEntity = resourcesGrantClient.queryAppResourceGrant(fields,filters,sorts,page,size);
            List<MRsAppResource> rsAppResources = responseEntity.getBody();
            Envelop envelop = getResult(rsAppResources, getTotalCount(responseEntity), page, size);
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

    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp, method = RequestMethod.POST)
    public Envelop grantRsMetaData(
            @ApiParam(name = "metadataId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "metadataId") String metadataId,
            @ApiParam(name = "appResourceId", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "appResourceId") String appResourceId) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAppResourceMetadata rsAppResourceMetadata = resourcesGrantClient.grantRsMetaData(metadataId,appResourceId);
            envelop.setObj(rsAppResourceMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrantApp, method = RequestMethod.POST)
    public Envelop grantRsMetaDataBatch(
            @ApiParam(name = "appResourceId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "appResourceId") String appResourceId,
            @ApiParam(name = "metadataIds", value = "资源数据元ID", defaultValue = "")
            @RequestParam(value = "metadataIds") String metadataIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsAppResourceMetadata> rsAppResourceMetadata = resourcesGrantClient.grantRsMetaDataBatch(appResourceId,metadataIds);
            envelop.setObj(rsAppResourceMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrant, method = RequestMethod.DELETE)
    public Envelop deleteMetadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteMetadataGrant(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants, method = RequestMethod.DELETE)
    public Envelop deleteMetadataGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteMetadataGrantBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元授权")
    public Envelop getRsMetadataGrantById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAppResourceMetadata rsAppResourceMetadata = resourcesGrantClient.getRsMetadataGrantById(id);
            envelop.setObj(rsAppResourceMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasGrants, method = RequestMethod.GET)
    public Envelop queryAppRsMetadataGrant(
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
            ResponseEntity<List<MRsAppResourceMetadata>> responseEntity = resourcesGrantClient.queryAppRsMetadataGrant(fields,filters,sorts,page,size);
            List<MRsAppResourceMetadata> rsAppResourceMetadatas = responseEntity.getBody();
            Envelop envelop = getResult(rsAppResourceMetadatas, getTotalCount(responseEntity), page, size);
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
