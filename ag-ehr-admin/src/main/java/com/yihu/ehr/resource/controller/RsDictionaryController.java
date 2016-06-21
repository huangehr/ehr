package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsDictionary;
import com.yihu.ehr.resource.client.RsDictionaryClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.utils.FeignExceptionUtils;
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
@Api(value = "dictionaries", description = "标准字典服务接口")
public class RsDictionaryController extends BaseController {

    @Autowired
    private RsDictionaryClient rsDictionaryClient;


    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典列表", notes = "根据查询条件获取标准字典列表")
    public Envelop searchRsDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
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


    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.POST)
    @ApiOperation(value = "创建标准字典", notes = "创建标准字典")
    public Envelop createRsDictionary(
            @ApiParam(name = "model", value = "", defaultValue = "")
            @RequestParam(value = "model") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsDictionary rsDictionary = rsDictionaryClient.createRsDictionary(jsonData);
            envelop.setObj(rsDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Dict, method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准字典", notes = "修改标准字典")
    public Envelop updateRsDictionary(
            @ApiParam(name = "model", value = "")
            @RequestParam(value = "model") String jsonData) throws Exception {
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


    @RequestMapping(value = ServiceApi.Resources.Dict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典", notes = "删除标准字典")
    public Envelop deleteRsDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsDictionaryClient.deleteRsDictionary(id);
            envelop.setSuccessFlg(true);
        } catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value =  ServiceApi.Resources.Dict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public Envelop getRsDictionaryById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") int id) {
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

    @RequestMapping(value = ServiceApi.Resources.DictBatch, method = RequestMethod.POST)
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

    @RequestMapping(value =  ServiceApi.Resources.DictExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public Envelop isExistence(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) {

        try {
            return success(rsDictionaryClient.isExistence(filters));
        }catch (Exception e){
            e.printStackTrace();
            return failed("查询出错！");
        }
    }

    @RequestMapping(value = ServiceApi.Resources.DictEntryBatch, method = RequestMethod.POST)
    @ApiOperation(value = "批量创建标准字典以及字典项", notes = "批量创建标准字典以及字典项")
    public Envelop createDictAndEntries(
            @ApiParam(name = "model", value = "", defaultValue = "")
            @RequestParam("model") String model) throws Exception {

        if(rsDictionaryClient.createDictAndEntries(model))
            return success("");
        return failed("新增出错！");
    }

    @RequestMapping(value = ServiceApi.Resources.DictCodesExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在字典编码")
    public List idExistence(
            @ApiParam(name = "codes", value = "", defaultValue = "")
            @RequestParam("codes") String codes) throws Exception {

        List existCodes = rsDictionaryClient.codeExistence(codes);
        return existCodes;
    }
}