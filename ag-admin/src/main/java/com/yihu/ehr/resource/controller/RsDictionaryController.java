package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsDictionary;
import com.yihu.ehr.resource.client.RsDictionaryClient;
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
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "dictionaries", description = "标准字典服务接口")
public class RsDictionaryController extends BaseController {

    @Autowired
    private RsDictionaryClient rsDictionaryClient;


    @RequestMapping(value = "/admin" + ServiceApi.Resources.Dicts, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典列表", notes = "根据查询条件获取标准字典列表")
    public Envelop searchRsDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        Envelop envelop = new Envelop();
        try {
            ResponseEntity<List<MRsDictionary>> responseEntity = rsDictionaryClient.searchRsDictionaries(fields,filters,sorts,page,size);
            List<MRsDictionary> rsDictionaries = responseEntity.getBody();
            envelop = getResult(rsDictionaries, getTotalCount(responseEntity), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = "/admin" + ServiceApi.Resources.Dicts, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准字典", notes = "创建标准字典")
    public Envelop createRsDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsDictionary rsDictionary = rsDictionaryClient.createRsDictionary(jsonData);
            envelop.setObj(rsDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/admin" + ServiceApi.Resources.Dicts, method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准字典", notes = "修改标准字典")
    public Envelop updateRsDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsDictionary rsDictionary = rsDictionaryClient.updateRsDictionary(jsonData);
            envelop.setObj(rsDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = "/admin" + ServiceApi.Resources.Dict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典", notes = "删除标准字典")
    public Envelop deleteRsDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsDictionaryClient.deleteRsDictionary(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/admin" + ServiceApi.Resources.Dict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public Envelop getRsDictionaryById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @RequestParam(value = "id") String id) {
        Envelop envelop = new Envelop();
        try{
            MRsDictionary rsDictionary = rsDictionaryClient.getRsDictionaryById(id);
            envelop.setObj(rsDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/admin" + ServiceApi.Resources.DictsBatch, method = RequestMethod.POST)
    @ApiOperation(value = "批量创建标准字典", notes = "批量创建标准字典")
    public Envelop createRsDictionaries(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsDictionaryClient.createRsDictionaries(jsonData);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

}