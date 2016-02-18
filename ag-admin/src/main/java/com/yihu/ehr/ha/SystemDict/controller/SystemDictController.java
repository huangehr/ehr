package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.SystemDict.service.SystemDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0)
@RestController
@Api(value = "sys_dict", description = "系统字典接口，用于系统全局字典管理", tags = {"系统字典接口"})
public class SystemDictController {

    @Autowired
    private static SystemDictClient systemDictClient;

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
       return systemDictClient.getDictionaries(fields,filters,sorts,size,page);
    }

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    public MSystemDict createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {
        return systemDictClient.createDictionary(dictJson);
    }

    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    public MSystemDict getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id){
        return systemDictClient.getDictionary(id);
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.PUT)
    public MSystemDict updateDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        return systemDictClient.updateDictionary(id,name);
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    public boolean deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        return systemDictClient.deleteDictionary(id);
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
        return systemDictClient.getDictEntries(id,value,page,rows);
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries", method = RequestMethod.POST)
    public MConventionalDict createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        return systemDictClient.createDictEntry(entryJson);
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.POST)
    public MDictionaryEntry getDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        return systemDictClient.getDictEntry(id,code);
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.DELETE)
    public Object deleteDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {
        return systemDictClient.deleteDictEntry(id,code);
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.PUT)
    public MConventionalDict updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        return systemDictClient.updateDictEntry(entryJson);
    }
}
