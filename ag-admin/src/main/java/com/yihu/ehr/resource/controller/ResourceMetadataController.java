package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsResourceMetadataModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.model.resource.MRsResourceMetadata;
import com.yihu.ehr.resource.client.MetadataClient;
import com.yihu.ehr.resource.client.ResourceMetadataClient;
import com.yihu.ehr.util.ArrayListUtil;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@Api(value = "ResourceMetadata", description = "资源数据元")
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
public class ResourceMetadataController extends BaseController {

    @Autowired
    private ResourceMetadataClient resourceMetadataClient;

    @Autowired
    private MetadataClient metadataClient;

    @ApiOperation("创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.POST)
    public Envelop createResourceMetadata(
            @ApiParam(name = "metadata", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "metadata") String metadata) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsResourceMetadata rsMetadata = resourceMetadataClient.createResourceMetadata(metadata);
            envelop.setObj(rsMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("批量创建资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatch, method = RequestMethod.POST)
    public Envelop createResourceMetadataBatch(
            @ApiParam(name = "metadatas", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "metadatas") String metadatas) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsResourceMetadata> rsMetadatas = resourceMetadataClient.createResourceMetadataBatch(metadatas);
            envelop.setObj(rsMetadatas);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("更新资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.PUT)
    public Envelop updateResourceMetadata(
            @ApiParam(name = "dimension", value = "资源数据元", defaultValue = "")
            @RequestParam(value = "dimension") String metadata) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsResourceMetadata rsMetadata = resourceMetadataClient.updateResourceMetadata(metadata);
            envelop.setObj(rsMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata, method = RequestMethod.DELETE)
    public Envelop deleteResourceMetadata(
            @ApiParam(name = "id", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourceMetadataClient.deleteResourceMetadata(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("根据ids批量资源数据元删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataBatch, method = RequestMethod.DELETE)
    public Envelop deleteResourceMetadataBatchById(
            @ApiParam(name = "ids", value = "ids", defaultValue = "")
            @RequestParam(value = "ids") String[] idArray) throws Exception {
        Envelop envelop = new Envelop();
        try{
            List<String> ids = ArrayListUtil.getList(idArray);
            resourceMetadataClient.deleteResourceMetadataBatchById(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("根据资源ID批量删除资源数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.DELETE)
    public Envelop deleteResourceMetadataBatch(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourceMetadataClient.deleteResourceMetadataBatch(resourceId);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadata,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元")
    public Envelop getRsMetadataById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsResourceMetadata rsResourceMetadata = resourceMetadataClient.getRsMetadataById(id);
            envelop.setObj(rsResourceMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/resources/{resources_id}/metadata_list", method = RequestMethod.GET)
    @ApiOperation("根据资源id(resourcesId)获取资源数据元列表")
    public Envelop getRsMetadataByResourcesId(
            @ApiParam(name = "resources_id", value = "resources_id", defaultValue = "")
            @PathVariable(value = "resources_id") String resourcesId) throws Exception {
        Envelop envelop = new Envelop();
        try{
            List<MRsResourceMetadata> metadataList  = resourceMetadataClient.getRsMetadataByResourcesId(resourcesId);
            envelop.setDetailModelList(metadataList);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataList, method = RequestMethod.GET)
    public Envelop queryDimensions(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(name = "fields", required = false) String fields,
            @ApiParam(name = "resources_id",value = "资源id",defaultValue = "")
            @RequestParam(name = "resources_id",required = false) String resourcesId,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(name = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(name = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(name = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(name = "size", required = false) int size) throws Exception {
        try
        {
            ResponseEntity<List<MRsResourceMetadata>> responseEntity = resourceMetadataClient.queryDimensions(fields,"resourcesId=" + resourcesId,sorts,page,size);
            List<MRsResourceMetadata> rsMetadatas = responseEntity.getBody();

            List<RsResourceMetadataModel> rsResourceMetadataModels = getRsMetadata(rsMetadatas,filters,page,size);

            Envelop envelop = getResult(rsResourceMetadataModels, getTotalCount(responseEntity), page, size);
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

    public List<RsResourceMetadataModel> getRsMetadata(List<MRsResourceMetadata> rsMetadatas,String filters,int page,int size){

        List<RsResourceMetadataModel> rsMetadataModels = new ArrayList<>();

        String fs = null;
        for (MRsResourceMetadata mrm : rsMetadatas){
            RsResourceMetadataModel rsMetadataModel = convertToModel(mrm,RsResourceMetadataModel.class);
            fs = "id=" + rsMetadataModel.getMetadataId()+" g2;"+filters;

            ResponseEntity<List<MRsMetadata>> responseEntity = metadataClient.getMetadata("",fs,"",1,size);
            List<MRsMetadata> mRsMetadatas = responseEntity.getBody();

            if (mRsMetadatas.size()>0) {
                rsMetadataModel.setName(mRsMetadatas.get(0).getName());
                rsMetadataModel.setColumnType(mRsMetadatas.get(0).getColumnType());
                rsMetadataModel.setStdCode(mRsMetadatas.get(0).getId());
                rsMetadataModels.add(rsMetadataModel);
            }
        }
        return rsMetadataModels;
    }
}
