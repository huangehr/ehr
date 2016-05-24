package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.resource.client.MetadataClient;
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
@Api(value = "metadata", description = "数据元服务接口")
public class MetadataController extends BaseController {

    @Autowired
    private MetadataClient metadataClient;

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.POST)
    @ApiOperation("创建数据元")
    public Envelop createMetadata(
            @ApiParam(name = "metadata", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "metadata") String metadata) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsMetadata rsMetadata = metadataClient.createMetadata(metadata);
            envelop.setObj(rsMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.MetadatasBatch, method = RequestMethod.POST)
    @ApiOperation("批量创建数据元")
    public Envelop createMetadataPatch(
            @ApiParam(name = "metadatas", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "metadatas") String metadatas) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsMetadata> rsMetadata = metadataClient.createMetadataPatch(metadatas);
            envelop.setObj(rsMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.PUT)
    @ApiOperation("更新数据元")
    public Envelop updateMetadata(
            @ApiParam(name = "metadata", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "metadata") String metadata) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsMetadata rsMetadata = metadataClient.updateMetadata(metadata);
            envelop.setObj(rsMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Metadata, method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public Envelop deleteMetadata(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            metadataClient.deleteMetadata(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.DELETE)
    @ApiOperation("批量删除数据元")
    public Envelop deleteMetadataBatch(
            @ApiParam(name = "ids", value = "数据元ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            metadataClient.deleteMetadataBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Metadata,method = RequestMethod.GET)
    @ApiOperation("根据ID获取数据元")
    public Envelop getMetadataById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsMetadata rsMetadata = metadataClient.getMetadataById(id);
            envelop.setObj(rsMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Metadatas, method = RequestMethod.GET)
    @ApiOperation("查询数据元")
    public Envelop getMetadata(
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
            ResponseEntity<List<MRsMetadata>> responseEntity = metadataClient.getMetadata(fields,filters,sorts,size,page);
            List<MRsMetadata> rsMetadatas = responseEntity.getBody();
            Envelop envelop = getResult(rsMetadatas, getTotalCount(responseEntity), page, size);
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
