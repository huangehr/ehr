package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAdapterDictionary;
import com.yihu.ehr.resource.client.RsAdapterDictionaryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.utils.FeignExceptionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0+"/admin")
@Api(value = "adapterDictionaries", description = "AdapterDictionary服务接口")
public class RsAdapterDictionaryController extends BaseController {

    @Autowired
    private RsAdapterDictionaryClient rsAdapterDictionaryClient;


    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取适配字典列表", notes = "根据查询条件获取适配字典列表")
    public Envelop searchRsAdapterDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        Envelop envelop = new Envelop();
        try {
            ResponseEntity<List<MRsAdapterDictionary>> responseEntity = rsAdapterDictionaryClient.searchRsAdapterDictionaries(fields,filters,sorts,page,size);
            List<MRsAdapterDictionary> adapterDictionaries = responseEntity.getBody();
            envelop = getResult(adapterDictionaries, getTotalCount(responseEntity), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }



    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建适配字典", notes = "创建适配字典")
    public Envelop createRsAdapterDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterDictionary adapterDictionary = rsAdapterDictionaryClient.createRsAdapterDictionary(jsonData);
            envelop.setObj(adapterDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改适配字典", notes = "修改适配字典")
    public Envelop updateRsAdapterDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterDictionary adapterDictionary = rsAdapterDictionaryClient.updateRsAdapterDictionary(jsonData);
            envelop.setObj(adapterDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Resources.AdapterDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配字典", notes = "删除适配字典")
    public Envelop deleteRsAdapterDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsAdapterDictionaryClient.deleteRsAdapterDictionary(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.AdapterDict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取适配字典")
    public Envelop getRsAdapterDictionaryById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @RequestParam(value = "id") String id) {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterDictionary adapterDictionary = rsAdapterDictionaryClient.getRsAdapterDictionaryById(id);
            envelop.setObj(adapterDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.AdapterDictsBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建适配字典", notes = "批量创建适配字典")
    public Envelop createRsAdapterDictionaries(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsAdapterDictionaryClient.createRsAdapterDictionaries(jsonData);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

}