package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import com.yihu.ehr.resource.client.AdapterMetadataClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "adapterMetadata", description = "适配数据元服务")
public class AdapterMetadataController extends BaseController {

    @Autowired
    private AdapterMetadataClient adapterMetadataClient;

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.POST)
    @ApiOperation("创建适配数据元")
    public Envelop createMetadata(
            @ApiParam(name = "adapterMetadata", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "adapterMetadata") String adapterMetadata) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterMetadata rsAdapterMetadata = adapterMetadataClient.createMetadata(adapterMetadata);
            envelop.setObj(rsAdapterMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.PUT)
    @ApiOperation("更新适配数据元")
    public Envelop updateMetadata(
            @ApiParam(name = "adapterMetadata", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "adapterMetadata") String adapterMetadata) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterMetadata rsAdapterMetadata = adapterMetadataClient.updateMetadata(adapterMetadata);
            envelop.setObj(rsAdapterMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.DELETE)
    @ApiOperation("删除适配数据元")
    public Envelop deleteMetadata(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            adapterMetadataClient.deleteMetadata(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配数据元")
    public Envelop deleteMetadataBatch(
            @ApiParam(name = "ids", value = "数据元ids", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            adapterMetadataClient.deleteMetadataBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadatas, method = RequestMethod.GET)
    @ApiOperation("查询适配数据元")
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
        ResponseEntity<List<MRsAdapterMetadata>> responseEntity = adapterMetadataClient.getMetadata(fields,filters,sorts,size,page);
        List<MRsAdapterMetadata> rsAdapterMetadatas = responseEntity.getBody();
        Envelop envelop = getResult(rsAdapterMetadatas, getTotalCount(responseEntity), page, size);
        return envelop;

    }
}
