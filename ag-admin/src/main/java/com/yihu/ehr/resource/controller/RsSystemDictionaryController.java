package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsSystemDictionary;
import com.yihu.ehr.resource.client.RsSystemDictionaryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
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
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "systemDictionaries", description = "系统字典服务接口")
public class RsSystemDictionaryController extends BaseController {

    @Autowired
    private RsSystemDictionaryClient rsSystemDictionaryClient;

    @RequestMapping(value = ServiceApi.Resources.SystemDicts, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典列表", notes = "根据查询条件获取系统字典列表")
    public Envelop searchRsSystemDictionaries(
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
            ResponseEntity<List<MRsSystemDictionary>> responseEntity = rsSystemDictionaryClient.searchRsSystemDictionaries(fields,filters,sorts,size,page);
            List<MRsSystemDictionary> rsSystemDictionaries = responseEntity.getBody();
            envelop = getResult(rsSystemDictionaries, getTotalCount(responseEntity), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }



    @RequestMapping(value = ServiceApi.Resources.SystemDicts, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典", notes = "创建系统字典")
    public Envelop createRsSystemDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsSystemDictionary rsSystemDictionary = rsSystemDictionaryClient.createRsSystemDictionary(jsonData);
            envelop.setObj(rsSystemDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDicts, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典", notes = "修改系统字典")
    public Envelop updateRsSystemDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsSystemDictionary rsSystemDictionary = rsSystemDictionaryClient.updateRsSystemDictionary(jsonData);
            envelop.setObj(rsSystemDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典", notes = "删除系统字典")
    public Envelop deleteRsSystemDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsSystemDictionaryClient.deleteRsSystemDictionary(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

}