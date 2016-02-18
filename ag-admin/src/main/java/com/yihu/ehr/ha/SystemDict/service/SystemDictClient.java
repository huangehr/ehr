package com.yihu.ehr.ha.SystemDict.service;

import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-dict")
public interface SystemDictClient {

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/rest/v1.0/dictionaries", method = RequestMethod.GET)
    Envelop getDictionaries(
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "phoneticCode", value = "首字母全拼", defaultValue = "")
            @RequestParam(value = "phoneticCode") String phoneticCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows) ;

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/rest/v1.0/dictionaries", method = RequestMethod.POST)
    MSystemDict createDictionary(
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "reference", value = "", defaultValue = "")
            @RequestParam(value = "reference") String reference,
            @ApiParam(name = "userId", value = "创建人", defaultValue = "")
            @RequestParam(value = "userId") String userId) ;

    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}", method = RequestMethod.GET)
    MSystemDict getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id);

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}", method = RequestMethod.PUT)
    MSystemDict updateDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name) ;

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}", method = RequestMethod.DELETE)
    boolean deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id);

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}/entries", method = RequestMethod.GET)
    Envelop getDictEntries(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) ;

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}/entries", method = RequestMethod.POST)
    MConventionalDict createDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "字典名称", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "sort", value = "排序号", defaultValue = "")
            @RequestParam(value = "sort") Integer sort,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog) ;

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}/entries/{code}", method = RequestMethod.POST)
    MDictionaryEntry getDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) ;

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}/entries/{code}", method = RequestMethod.DELETE)
    Object deleteDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) ;

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/rest/v1.0/dictionaries/{id}/entries/{code}", method = RequestMethod.PUT)
    MConventionalDict updateDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "value", value = "字典名称", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "sort", value = "排序号", defaultValue = "")
            @RequestParam(value = "sort") Integer sort,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog);
}
