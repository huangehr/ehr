package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.SystemDict.service.SystemDictClient;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/sysDict")
@RestController
public class SystemDictController extends BaseRestController {

    @Autowired
    private static SystemDictClient systemDictClient;

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public Object createDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "name", value = "字典名称")
                             @RequestParam(value = "name") String name,
                             @ApiParam(name = "reference", value = "")
                             @RequestParam(value = "reference") String reference,
                             @ApiParam(name = "userId", value = "用户ID")
                             @RequestParam(value = "userId") String userId) {
        return systemDictClient.createDict(apiVersion,name,reference,userId);
    }
    @RequestMapping(value = "/",method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "dictId", value = "字典ID")
                             @RequestParam(value = "dictId") long dictId) {

        return systemDictClient.deleteDict(apiVersion,dictId);
    }

    @RequestMapping(value = "/",method = RequestMethod.PUT)
    public Object updateDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dictId",value = "字典ID")
            @RequestParam(value = "dictId")long dictId,
            @ApiParam(name = "name",value = "字典名称")
            @RequestParam(value = "name")String name){

        return systemDictClient.updateDict(apiVersion,dictId,name);
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public Object getDictsByName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "name", value = "字典名称")
                                 @RequestParam(value = "dictName") String dictName,
                                 @ApiParam(name = "phoneticCode", value = "首字母全拼", defaultValue = "")
                                 @RequestParam(value = "phoneticCode") String phoneticCode,
                                 @ApiParam(name = "page", value = "当前页", defaultValue = "")
                                 @RequestParam(value = "page") int page,
                                 @ApiParam(name = "rows", value = "页数", defaultValue = "")
                                 @RequestParam(value = "rows") int rows) {
        return systemDictClient.searchSysDicts(apiVersion,dictName,phoneticCode,page,rows);
    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.POST)
    public Object createDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "api_version") String apiVersion,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") Long dictId,
                                  @ApiParam(name = "code", value = "字典项代码")
                                  @RequestParam(value = "code") String code,
                                  @ApiParam(name = "value", value = "值")
                                  @RequestParam(value = "value") String value,
                                  @ApiParam(name = "sort", value = "排序")
                                  @RequestParam(value = "sort") Integer sort,
                                  @ApiParam(name = "catalog", value = "分类")
                                  @RequestParam(value = "catalog") String catalog) {

        return systemDictClient.createDictEntry(apiVersion,dictId,code,value,sort,catalog);
    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.DELETE)
    public Object deleteDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") Long dictId,
                                  @ApiParam(name = "code", value = "字典项代码")
                                      @RequestParam(value = "code") String code){

        return systemDictClient.deleteDictEntry(apiVersion,dictId,code);
    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.POST)
    public Object updateDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "api_version") String apiVersion,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") Long dictId,
                                  @ApiParam(name = "code", value = "字典项代码")
                                  @RequestParam(value = "code") String code,
                                  @ApiParam(name = "value", value = "值")
                                  @RequestParam(value = "value") String value,
                                  @ApiParam(name = "sort", value = "排序")
                                  @RequestParam(value = "sort") Integer sort,
                                  @ApiParam(name = "catalog", value = "分类")
                                  @RequestParam(value = "catalog") String catalog) {
        return systemDictClient.updateDictEntry(apiVersion,dictId,code,value,sort,catalog);
    }

    @RequestMapping(value = "/dictEntrys",method = RequestMethod.GET)
    public Object getDictEntrysByDictId(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                        @PathVariable(value = "api_version") String apiVersion,
                                        @ApiParam(name = "dictId", value = "字典ID")
                                        @RequestParam(value = "dictId") long dictId,
                                        @ApiParam(name = "page", value = "当前页", defaultValue = "")
                                        @RequestParam(value = "page") int page,
                                        @ApiParam(name = "rows", value = "页数", defaultValue = "")
                                        @RequestParam(value = "rows") int rows) {

        return systemDictClient.searchDictEntryList(apiVersion,dictId,page,rows);
    }

    @RequestMapping(value = "/allDictEntry",method = RequestMethod.GET)
    public Object getDictEntrysByDictId(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "dictId", value = "字典ID")
                                 @RequestParam(value = "dictId") Long dictId){

        return systemDictClient.searchDictEntryListForDDL(apiVersion,dictId);
    }

    @RequestMapping(value = "/validator",method = RequestMethod.GET)
    public Object validator(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "api_version") String apiVersion,
                              @ApiParam(name = "code", value = "字典名称")
                              @RequestParam(value = "code") String code){
        return systemDictClient.validator(apiVersion,code);
    }

    @RequestMapping(value = "/autoSearchDictEntryList", method = RequestMethod.GET)
    public Object autoSearchDictEntryList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                      @ApiParam(name = "dictId", value = "字典ID")
                                      @RequestParam(value = "dictId") Long dictId,
                                      @ApiParam(name = "dictValue", value = "字典项值")
                                      @RequestParam(value = "dictValue") String dictValue) {

        return systemDictClient.autoSearchDictEntryList(apiVersion,dictId,dictValue);
    }
}
