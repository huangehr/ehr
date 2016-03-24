package com.yihu.ehr.ha.SystemDict.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(name=MicroServices.Dictionary)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface SystemDictClient {

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    ResponseEntity<Collection<MSystemDict>> getDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page) ;

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    MSystemDict createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) ;

    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    MSystemDict getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id);

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.PUT)
    MSystemDict updateDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) ;

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    boolean deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id);

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.GET)
    ResponseEntity<List<MDictionaryEntry>> getDictEntries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page) ;

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.POST)
    MConventionalDict createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) ;

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.GET)
    MDictionaryEntry getDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) ;

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.DELETE)
    boolean deleteDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) ;

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT)
    MConventionalDict updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson);

    @RequestMapping(value = "/dictionaries/existence/{dict_name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isDictNameExists(
            @ApiParam(name = "dict_name", value = "dict_name", defaultValue = "")
            @PathVariable(value = "dict_name") String dictName);

    @RequestMapping(value = "/dictionaries/existence/{dict_id}/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根基dictId和code判断提交的字典项名称是否已经存在")
    boolean isDictEntryCodeExists(
            @ApiParam(name = "dict_id", value = "dict_id", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code);
}
