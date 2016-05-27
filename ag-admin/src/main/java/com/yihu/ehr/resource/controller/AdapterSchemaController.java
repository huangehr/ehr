package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.resource.MRsAdapterSchema;
import com.yihu.ehr.resource.client.AdapterSchemaClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
@Api(value = "adapterSchema", description = "适配方案服务")
public class AdapterSchemaController extends BaseController {

    @Autowired
    private AdapterSchemaClient adapterSchemaClient;

    @Autowired
    private ConventionalDictEntryClient dictClint;

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.POST)
    @ApiOperation("创建适配方案")
    public Envelop createSchema(
            @ApiParam(name = "adapterSchema", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "adapterSchema") String adapterSchema) throws Exception {
        Envelop envelop = new Envelop();
        try
        {
            MRsAdapterSchema rsAdapterSchema = adapterSchemaClient.createSchema(adapterSchema);
            envelop.setObj(rsAdapterSchema);
            envelop.setSuccessFlg(true);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }

        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.PUT)
    @ApiOperation("更新适配方案")
    public Envelop updateSchema(
            @ApiParam(name = "adapterSchema", value = "数据元JSON", defaultValue = "")
            @RequestParam(value = "adapterSchema") String adapterSchema) throws Exception {
        Envelop envelop = new Envelop();
        try
        {
            MRsAdapterSchema rsAdapterSchema = adapterSchemaClient.updateSchema(adapterSchema);
            envelop.setObj(rsAdapterSchema);
            envelop.setSuccessFlg(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }

        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schema, method = RequestMethod.DELETE)
    @ApiOperation("删除适配方案")
    public Envelop deleteSchema(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            adapterSchemaClient.deleteSchema(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.DELETE)
    @ApiOperation("批量删除适配方案")
    public Envelop deleteSchemaBatch(
            @ApiParam(name = "ids", value = "数据元ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            adapterSchemaClient.deleteSchemaBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schema,method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配方案")
    public Envelop getMetadataById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterSchema rsAdapterSchema= adapterSchemaClient.getAdapterSchemaById(id);
            MConventionalDict adapterType=  dictClint.getResourceAdaptScheme(rsAdapterSchema.getType());
            rsAdapterSchema.setTypeName(adapterType.getValue());
            envelop.setObj(rsAdapterSchema);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Adaptions.Schemas, method = RequestMethod.GET)
    @ApiOperation("查询适配方案")
    public Envelop getSchema(
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
            ResponseEntity<List<MRsAdapterSchema>> responseEntity = adapterSchemaClient.getSchema(fields,filters,sorts,page,size);
            List<MRsAdapterSchema> rsAdapterSchemas = responseEntity.getBody();
            for(MRsAdapterSchema mRsAdapterSchema: rsAdapterSchemas){
                if(StringUtils.isNotBlank(mRsAdapterSchema.getType())){
                    MConventionalDict adapterType=  dictClint.getResourceAdaptScheme(mRsAdapterSchema.getType());
                    mRsAdapterSchema.setTypeName(adapterType.getValue());
                }
            }
            Envelop envelop = getResult(rsAdapterSchemas, getTotalCount(responseEntity), page, size);
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
