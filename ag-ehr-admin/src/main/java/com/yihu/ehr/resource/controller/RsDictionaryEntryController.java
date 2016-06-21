package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsDictionaryEntry;
import com.yihu.ehr.resource.client.RsDictionaryEntryClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.utils.FeignExceptionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "dictionaryEntries", description = "标准字典项服务接口")
public class RsDictionaryEntryController extends BaseController {
    @Autowired
    private RsDictionaryEntryClient rsDictionaryEntryClient;

    @RequestMapping(value = ServiceApi.Resources.DictEntries, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典项列表", notes = "根据查询条件获取标准字典项列表")
    public Envelop searchRsDictionaryEntries(
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
            ResponseEntity<List<MRsDictionaryEntry>> responseEntity = rsDictionaryEntryClient.searchRsDictionaryEntries(fields, filters, sorts, page, size);
            List<MRsDictionaryEntry> rsDictionaryEntries = responseEntity.getBody();
            envelop = getResult(rsDictionaryEntries, getTotalCount(responseEntity), page, size);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }

        return envelop;
    }


    @RequestMapping(value = ServiceApi.Resources.DictEntries, method = RequestMethod.POST)
    @ApiOperation(value = "创建标准字典项", notes = "创建标准字典项")
    public Envelop createRsDictionaryEntry(
            @ApiParam(name = "model", value = "", defaultValue = "")
            @RequestParam(value = "model") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsDictionaryEntry rsDictionaryEntry = rsDictionaryEntryClient.createRsDictionaryEntry(jsonData);
            envelop.setObj(rsDictionaryEntry);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.DictEntries, method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准字典项", notes = "修改标准字典项")
    public Envelop updateRsDictionaryEntry(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "model") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsDictionaryEntry rsDictionaryEntry = rsDictionaryEntryClient.updateRsDictionaryEntry(jsonData);
            envelop.setObj(rsDictionaryEntry);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.DictEntry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典项", notes = "删除标准字典项")
    public Envelop deleteRsDictionaryEntry(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            rsDictionaryEntryClient.deleteRsDictionaryEntry(id);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.DictEntry, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public Envelop getRsDictionaryEntryById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        try {
            MRsDictionaryEntry rsDictionaryEntry = rsDictionaryEntryClient.getRsDictionaryEntryById(id);
            envelop.setObj(rsDictionaryEntry);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.DictEntriesByDictCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据dict_code获取获取标准字典")
    public Envelop getRsDictionaryEntryByDictCode(
            @ApiParam(name = "dict_code", value = "", defaultValue = "")
            @PathVariable(value = "dict_code") String dict_code) {
        Envelop envelop = new Envelop();
        try{
            List<MRsDictionaryEntry> rsDictionaryEntries = rsDictionaryEntryClient.getRsDictionaryEntryByDictCode(dict_code);
            envelop.setObj(rsDictionaryEntries);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.DictEntriesExistence, method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public Envelop isExistence(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters) {

        try {
            return success(rsDictionaryEntryClient.isExistence(filters));
        } catch (Exception e) {
            e.printStackTrace();
            return failed("查询出错！");
        }
    }

    @RequestMapping(value = "/resources/noPageDictEntries", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典项列表_不分页", notes = "根据查询条件获取标准字典项列表_不分页")
    public Envelop searchNoPageRsDictEntries(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        List<MRsDictionaryEntry> dictionaryEntries = rsDictionaryEntryClient.searchNoPageRsDictEntries(filters);
        if (dictionaryEntries.size() > 0) {
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(dictionaryEntries);
        } else
            envelop.setErrorMsg("字典查询失败");

        return envelop;
    }
}