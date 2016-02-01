package com.yihu.ehr.ha.SystemDict.service;

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

    @ApiOperation(value = "创建字典")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/", method = RequestMethod.POST)
    Object createDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                      @PathVariable(value = "api_version") String apiVersion,
                      @ApiParam(name = "name", value = "名称", defaultValue = "")
                      @RequestParam(value = "name") String name,
                      @ApiParam(name = "reference", value = "", defaultValue = "")
                      @RequestParam(value = "reference") String reference,
                      @ApiParam(name = "userId", value = "创建人", defaultValue = "")
                      @RequestParam(value = "userId") String userId);

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/", method = RequestMethod.DELETE)
    Object deleteDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                      @PathVariable(value = "api_version") String apiVersion,
                      @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                      @RequestParam(value = "dictId") long dictId);

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/", method = RequestMethod.PUT)
    Object updateDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                      @PathVariable(value = "api_version") String apiVersion,
                      @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                      @RequestParam(value = "dictId") long dictId,
                      @ApiParam(name = "name", value = "字典名称", defaultValue = "")
                      @RequestParam(value = "name") String name);

    @ApiOperation(value = "查询字典列表")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/search", method = RequestMethod.GET)
    Object searchSysDicts(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                          @PathVariable(value = "api_version") String apiVersion,
                          @ApiParam(name = "name", value = "字典名称", defaultValue = "")
                          @RequestParam(value = "name") String name,
                          @ApiParam(name = "phoneticCode", value = "首字母全拼", defaultValue = "")
                          @RequestParam(value = "phoneticCode") String phoneticCode,
                          @ApiParam(name = "page", value = "当前页", defaultValue = "")
                          @RequestParam(value = "page") int page,
                          @ApiParam(name = "rows", value = "行数", defaultValue = "")
                          @RequestParam(value = "rows") int rows);

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/dict_entry", method = RequestMethod.POST)
    Object createDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "api_version") String apiVersion,
                           @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                           @RequestParam(value = "dictId") long dictId,
                           @ApiParam(name = "code", value = "字典编号", defaultValue = "")
                           @RequestParam(value = "code") String code,
                           @ApiParam(name = "value", value = "字典名称", defaultValue = "")
                           @RequestParam(value = "value") String value,
                           @ApiParam(name = "sort", value = "排序号", defaultValue = "")
                           @RequestParam(value = "sort") Integer sort,
                           @ApiParam(name = "catalog", value = "类别", defaultValue = "")
                           @RequestParam(value = "catalog") String catalog);

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/dict_entry", method = RequestMethod.DELETE)
    Object deleteDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "api_version") String apiVersion,
                           @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                           @RequestParam(value = "dictId") long dictId,
                           @ApiParam(name = "code", value = "字典编号", defaultValue = "")
                           @RequestParam(value = "code") String code);

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/dict_entry", method = RequestMethod.PUT)
    Object updateDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "api_version") String apiVersion,
                           @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                           @RequestParam(value = "dictId") long dictId,
                           @ApiParam(name = "code", value = "字典编号", defaultValue = "")
                           @RequestParam(value = "code") String code,
                           @ApiParam(name = "value", value = "字典名称", defaultValue = "")
                           @RequestParam(value = "value") String value,
                           @ApiParam(name = "sort", value = "排序号", defaultValue = "")
                           @RequestParam(value = "sort") Integer sort,
                           @ApiParam(name = "catalog", value = "类别", defaultValue = "")
                           @RequestParam(value = "catalog") String catalog);

    @ApiOperation(value = "查询字典项列表")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/dict_entry/search", method = RequestMethod.GET)
    Object searchDictEntryList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                               @PathVariable(value = "api_version") String apiVersion,
                               @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                               @RequestParam(value = "dictId") long dictId,
                               @ApiParam(name = "page", value = "当前页", defaultValue = "")
                               @RequestParam(value = "page") Integer page,
                               @ApiParam(name = "rows", value = "行数", defaultValue = "")
                               @RequestParam(value = "rows") Integer rows);


    @ApiOperation(value = "获取标签项")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/select_tags", method = RequestMethod.GET)
    Object selectTags();

    @ApiOperation(value = "根据字典编号获取字典项")
    @RequestMapping(value = "/rest/{api_version}/sys_dict/dict_entry_list_ddl", method = RequestMethod.GET)
    Object searchDictEntryListForDDL(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "api_version") String apiVersion,
                                     @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
                                     @RequestParam(value = "dictId") long dictId);

    @ApiOperation(value = "根据字典名称判断字典是否已存在")
    @RequestMapping("/rest/{api_version}/sys_dict/validator")
    Object validator(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                     @PathVariable(value = "api_version") String apiVersion,
                     @ApiParam(name = "name", value = "字典名称", defaultValue = "")
                     @RequestParam(value = "name") String name);

    @ApiOperation(value = "根据字典编号和字典名称查询字典列表，模糊查询")
    @RequestMapping("/rest/{api_version}/sys_dict/autoSearchDictEntryList")
    Object autoSearchDictEntryList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "api_version") String apiVersion,
                                   @ApiParam(name = "dictId", value = "字典项编号", defaultValue = "")
                                   @RequestParam(value = "dictId") Long dictId,
                                   @ApiParam(name = "value", value = "字典项值", defaultValue = "")
                                   @RequestParam(value = "value") String value);
}
