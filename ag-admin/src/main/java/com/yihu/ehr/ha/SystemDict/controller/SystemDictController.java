package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.SystemDict.service.SystemDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.model.dict.UIModels.SystemDictModel;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0)
@RestController
@Api(value = "sys_dict", description = "系统字典接口，用于系统全局字典管理", tags = {"系统字典接口"})
public class SystemDictController {

    @Autowired
    private SystemDictClient systemDictClient;

//    @Autowired
//    private BaseRestController baseRestController;

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    public Envelop getDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page) {

        Envelop envelop = new Envelop();

        //todo: 微服务缺少获取字典总条数的接口
        //todo： 前端的分页需要用到ListModel，totalCount，page，rows属性，但是 Envelop 工具类没有page，rows属性？
        List<MSystemDict> systemDicts =(List<MSystemDict>)systemDictClient.getDictionaries(fields,filters,sorts,size,page);

        envelop.setDetailModelList(systemDicts);


       return envelop;
    }

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    public Envelop createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        Envelop envelop = new Envelop();
        MSystemDict systemDict = systemDictClient.createDictionary(dictJson);

        envelop.setObj(systemDict);

        return envelop;
    }


    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    public Envelop getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id){
        Envelop envelop = new Envelop();

        MSystemDict systemDict = systemDictClient.getDictionary(id);

        envelop.setObj(systemDict);
        return envelop;
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.PUT)
    public Envelop updateDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {
        Envelop envelop = new Envelop();

        MSystemDict systemDict = systemDictClient.updateDictionary(dictJson);

        envelop.setObj(systemDict);

        return envelop;
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    public Envelop deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        Envelop envelop = new Envelop();

        Boolean bo = systemDictClient.deleteDictionary(id);

        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/{id}/entries", method = RequestMethod.GET)
    public Envelop getDictEntries(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) {

        Envelop envelop = new Envelop();
        List<MDictionaryEntry> dictionaryEntries = (List<MDictionaryEntry>)systemDictClient.getDictEntries(id,value,page,rows);
        //todo: 微服务缺少获取字典项总条数的接口

        envelop.setDetailModelList(dictionaryEntries);

        return envelop;
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.POST)
    public Envelop createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict= systemDictClient.createDictEntry(entryJson);

        envelop.setObj(mConventionalDict);
        return envelop;
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.POST)
    public Envelop getDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        Envelop envelop = new Envelop();

        MDictionaryEntry mDictionaryEntry= systemDictClient.getDictEntry(id, code);

        envelop.setObj(mDictionaryEntry);
        return envelop;
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.DELETE)
    public Envelop deleteDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {
        Envelop envelop = new Envelop();

        Boolean bo= systemDictClient.deleteDictEntry(id, code);

        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT)
    public Envelop updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict= systemDictClient.updateDictEntry(entryJson);

        envelop.setObj(mConventionalDict);

        return envelop;
    }
}
