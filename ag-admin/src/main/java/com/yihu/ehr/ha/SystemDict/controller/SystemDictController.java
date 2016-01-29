package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/sysDict")
@RestController
public class SystemDictController extends BaseRestController {

    @RequestMapping(value = "/createDict",method = RequestMethod.POST)
    public Object createDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "name", value = "字典名称")
                             @RequestParam(value = "name") String name,
                             @ApiParam(name = "reference", value = "")
                             @RequestParam(value = "reference") String reference,
                             @ApiParam(name = "userId", value = "用户ID")
                             @RequestParam(value = "userId") String userId) {
        return null;
    }
    @RequestMapping(value = "/deleteDict",method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "dictId", value = "字典ID")
                             @RequestParam(value = "dictId") long dictId) {

        return null;
    }

    @RequestMapping(value = "/updateDict",method = RequestMethod.POST)
    public Object updateDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dictId",value = "字典ID")
            @RequestParam(value = "dictId")long dictId,
            @ApiParam(name = "name",value = "字典名称")
            @RequestParam(value = "name")String name){

        return null;
    }

    @RequestMapping(value = "/dicts",method = RequestMethod.GET)
    public Object getDictsByName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "name", value = "字典名称")
                                 @RequestParam(value = "dictName") String dictName,
                                 @ApiParam(name = "page", value = "当前页", defaultValue = "")
                                 @RequestParam(value = "page") Integer page,
                                 @ApiParam(name = "rows", value = "页数", defaultValue = "")
                                 @RequestParam(value = "rows") Integer rows) {
        return null;
    }

    @RequestMapping(value = "/createDictEntry",method = RequestMethod.POST)
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

        return null;
    }

    @RequestMapping(value = "/deleteDictEntry",method = RequestMethod.DELETE)
    public Object deleteDictEntry(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") Long dictId,
                                  @ApiParam(name = "code", value = "字典项代码")
                                      @RequestParam(value = "code") String code){

        return null;
    }

    @RequestMapping(value = "/updateDictEntry",method = RequestMethod.POST)
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
        return null;
    }

    @RequestMapping(value = "/dictEntrys",method = RequestMethod.GET)
    public Object getDictEntrysByDictId(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                        @PathVariable(value = "api_version") String apiVersion,
                                        @ApiParam(name = "dictId", value = "字典ID")
                                        @RequestParam(value = "dictId") Long dictId,
                                        @ApiParam(name = "page", value = "当前页", defaultValue = "")
                                        @RequestParam(value = "page") Integer page,
                                        @ApiParam(name = "rows", value = "页数", defaultValue = "")
                                        @RequestParam(value = "rows") Integer rows) {

        return null;
    }

    @RequestMapping(value = "/allDictEntry",method = RequestMethod.GET)
    public Object getDictEntrysByDictId(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "dictId", value = "字典ID")
                                 @RequestParam(value = "dictId") Long dictId){

        return null;
    }

    @RequestMapping(value = "/isExistDict",method = RequestMethod.GET)
    public Object isExistDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "api_version") String apiVersion,
                              @ApiParam(name = "code", value = "字典名称")
                              @RequestParam(value = "code") String code){
        return null;
    }

    @RequestMapping(value = "/getDictEntryByVaule", method = RequestMethod.GET)
    public Object getDictEntryByValue(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                      @ApiParam(name = "dictId", value = "字典ID")
                                      @RequestParam(value = "dictId") Long dictId,
                                      @ApiParam(name = "dictName", value = "字典项名称")
                                      @RequestParam(value = "dictName") String dictName) {

        return null;
    }
}
