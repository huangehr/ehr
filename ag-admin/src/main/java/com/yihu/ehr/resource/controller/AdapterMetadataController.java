package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsAdapterMetadataModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.resource.client.AdapterMetadataClient;
import com.yihu.ehr.resource.client.MetadataClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
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
@Api(value = "adapterMetadata", description = "适配数据元服务")
public class AdapterMetadataController extends BaseController {

    @Autowired
    private AdapterMetadataClient adapterMetadataClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictEntryClient;

    @Autowired
    private MetadataClient metadataClient;

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.POST)
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

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.PUT)
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

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.DELETE)
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

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata,method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配数据元")
    public Envelop getMetadataById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterMetadata rsAdapterMetadata = adapterMetadataClient.getMetadataById(id);
            envelop.setObj(rsAdapterMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.GET)
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
        try
        {
            ResponseEntity<List<MRsAdapterMetadata>> responseEntity = adapterMetadataClient.getMetadata(fields, filters, sorts, page, size);
            List<MRsAdapterMetadata> rsAdapterMetadatas = responseEntity.getBody();
            List<RsAdapterMetadataModel> rsAdapterMetadataModels = new ArrayList<RsAdapterMetadataModel>();
            for (MRsAdapterMetadata rsAdapterMetadata: rsAdapterMetadatas){
                RsAdapterMetadataModel rsAdapterMetadataModel = new RsAdapterMetadataModel();
                BeanUtils.copyProperties(rsAdapterMetadata,rsAdapterMetadataModel);
                if(StringUtils.isNotBlank(rsAdapterMetadata.getMetadataDomain())){
                    MConventionalDict mConventionalDict = conventionalDictEntryClient.getBusinessDomain(rsAdapterMetadata.getMetadataDomain());
                    if(mConventionalDict!=null){
                        rsAdapterMetadataModel.setMetadataDomainName(mConventionalDict.getValue());
                    }
                }
                if(StringUtils.isNotBlank(rsAdapterMetadata.getMetadataId())){
                    MRsMetadata mRsMetadata =  metadataClient.getMetadataById(rsAdapterMetadata.getMetadataId());
                    rsAdapterMetadataModel.setMetadataName(mRsMetadata.getName());
                }
                rsAdapterMetadataModels.add(rsAdapterMetadataModel);
            }
            Envelop envelop = getResult(rsAdapterMetadataModels, getTotalCount(responseEntity), page, size);
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

    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadataBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建适配数据元", notes = "批量创建适配数据元")
    public Envelop createRsMetaDataBatch(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterMetadata rsAdapterMetadata = adapterMetadataClient.createRsMetaDataBatch(jsonData);
            envelop.setObj(rsAdapterMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
}
