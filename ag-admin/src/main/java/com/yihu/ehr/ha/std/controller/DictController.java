package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/dict")
@RestController
public class DictController extends BaseRestController {

    @RequestMapping(value = "/dicts",method = RequestMethod.GET)
    public Object getDictsByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                       @PathVariable(value = "apiVersion") String apiVersion,
                                       @ApiParam(name = "versionCode", value = "标准版本代码")
                                       @RequestParam(value = "versionCode") String versionCode,
                                       @ApiParam(name = "code",value = "字典代码")
                                       @RequestParam(value = "code")String code,
                                       @ApiParam(name = "name",value = "字典名称")
                                       @RequestParam(value = "name")String name){
        return null;
    }

    @RequestMapping(value = "/dict",method = RequestMethod.GET)
    public Object getDictById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "apiVersion") String apiVersion,
                              @ApiParam(name = "versionCode", value = "标准版本代码")
                              @RequestParam(value = "versionCode") String versionCode,
                              @ApiParam(name = "dictId",value = "字典ID")
                              @RequestParam(value = "dictId")String dictId){
        return null;
    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.GET)
    public Object getDictEntryById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion,
                                   @ApiParam(name = "versionCode", value = "标准版本代码")
                                   @RequestParam(value = "versionCode") String versionCode,
                                   @ApiParam(name="dictId",value = "字典ID")
                                   @RequestParam(value = "dictId")String dictId,
                                   @ApiParam(name="entryId",value = "字典项ID")
                                   @RequestParam(value = "entryId")String entryId){
        return null;
    }

    @RequestMapping(value = "/createDict",method = RequestMethod.POST)
    public Object createDict(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "apiVersion") String apiVersion,
                           @ApiParam(name = "versionCode", value = "标准版本代码")
                           @RequestParam(value = "versionCode") String versionCode,
                           @ApiParam(name = "dictId", value = "字典ID")
                           @RequestParam(value = "dictId") String dictId,
                           @ApiParam(name = "code", value = "字典代码")
                           @RequestParam(value = "code") String code,
                           @ApiParam(name = "name", value = "字典名称")
                           @RequestParam(value = "name") String name,
                           @ApiParam(name = "baseDict", value = "父级字典ID")
                           @RequestParam(value = "baseDict") String baseDict,
                           @ApiParam(name = "stdSource", value = "标准来源ID")
                           @RequestParam(value = "stdSource") String stdSource,
                           @ApiParam(name = "stdVersion", value = "标准版本号")
                           @RequestParam(value = "stdVersion") String stdVersion,
                           @ApiParam(name = "description", value = "说明")
                           @RequestParam(value = "description") String description,
                           @ApiParam(name = "userId", value = "用户ID")
                           @RequestParam(value = "userId") String userId) {
        return null;
    }

    @RequestMapping(value = "/updateDict",method = RequestMethod.POST)
    public Object updateDict(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "apiVersion") String apiVersion,
                             @ApiParam(name = "versionCode", value = "标准版本代码")
                             @RequestParam(value = "versionCode") String versionCode,
                             @ApiParam(name = "dictId", value = "字典ID")
                             @RequestParam(value = "dictId") String dictId,
                             @ApiParam(name = "code", value = "字典代码")
                             @RequestParam(value = "code") String code,
                             @ApiParam(name = "name", value = "字典名称")
                             @RequestParam(value = "name") String name,
                             @ApiParam(name = "baseDict", value = "父级字典ID")
                             @RequestParam(value = "baseDict") String baseDict,
                             @ApiParam(name = "stdSource", value = "标准来源ID")
                             @RequestParam(value = "stdSource") String stdSource,
                             @ApiParam(name = "stdVersion", value = "标准版本号")
                             @RequestParam(value = "stdVersion") String stdVersion,
                             @ApiParam(name = "description", value = "说明")
                             @RequestParam(value = "description") String description,
                             @ApiParam(name = "userId", value = "用户ID")
                             @RequestParam(value = "userId") String userId){
        return null;
    }

    @RequestMapping(value = "deleteDict",method = RequestMethod.DELETE)
    public Object deleteDict(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "apiVersion") String apiVersion,
                             @ApiParam(name = "versionCode", value = "标准版本代码")
                             @RequestParam(value = "versionCode") String versionCode,
                             @ApiParam(name = "dictId", value = "字典ID")
                             @RequestParam(value = "dictId") String dictId) {

        return null;
    }

    @RequestMapping(value = "/getCdaDictList",method = RequestMethod.GET)
    public Object getCdaDictList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "versionCode", value = "标准版本代码")
                                 @RequestParam(value = "versionCode") String versionCode,
                                 @ApiParam(name = "code", value = "字典代码")
                                 @RequestParam(value = "code") String code,
                                 @ApiParam(name = "name", value = "字典名称")
                                 @RequestParam(value = "name") String name,
                                 @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                 @RequestParam(value = "page") int page,
                                 @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                 @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/getCdaBaseDictList",method = RequestMethod.GET)
    public Object getCdaBaseDictList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "apiVersion") String apiVersion,
                                     @ApiParam(name = "versionCode", value = "标准版本代码")
                                     @RequestParam(value = "versionCode") String versionCode,
                                     @ApiParam(name = "dictId", value = "字典ID")
                                     @RequestParam(value = "dictId") String dictId,
                                     @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                     @RequestParam(value = "page") int page,
                                     @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                     @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/createDictEntry",method = RequestMethod.POST)
      public Object createDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "apiVersion") String apiVersion,
                                    @ApiParam(name = "versionCode", value = "标准版本代码")
                                    @RequestParam(value = "versionCode") String versionCode,
                                    @ApiParam(name = "dictId", value = "字典ID")
                                    @RequestParam(value = "dictId") String dictId,
                                    @ApiParam(name = "entryId",value = "字典项ID")
                                    @RequestParam(value = "entryId")String entryId,
                                    @ApiParam(name = "entryCode",value = "字典项代码")
                                    @RequestParam(value = "entryCode")String entryCode,
                                    @ApiParam(name = "entryValue",value = "字典项值")
                                    @RequestParam(value = "entryValue")String entryValue,
                                    @ApiParam(name = "description",value = "说明")
                                    @RequestParam(value = "description")String description) {
        return null;
    }

    @RequestMapping(value = "/updateDictEntry",method = RequestMethod.POST)
    public Object updateDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "versionCode", value = "标准版本代码")
                                  @RequestParam(value = "versionCode") String versionCode,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") String dictId,
                                  @ApiParam(name = "entryId",value = "字典项ID")
                                  @RequestParam(value = "entryId")String entryId,
                                  @ApiParam(name = "entryCode",value = "字典项代码")
                                  @RequestParam(value = "entryCode")String entryCode,
                                  @ApiParam(name = "entryValue",value = "字典项值")
                                  @RequestParam(value = "entryValue")String entryValue,
                                  @ApiParam(name = "description",value = "说明")
                                  @RequestParam(value = "description")String description) {
        return null;
    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项", produces = "application/json", notes = "删除字典项信息，多ID删除时，Id以逗号隔开")
    public Object deleteDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "versionCode", value = "标准版本代码")
                                  @RequestParam(value = "versionCode") String versionCode,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") String dictId,
                                  @ApiParam(name = "entryIds", value = "字典项ID")
                                  @RequestParam(value = "entryIds") String entryIds) {
        return null;
    }

    @RequestMapping(value = "/dictEntrys",method = RequestMethod.GET)
    public Object getDictEntryByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                           @PathVariable(value = "apiVersion") String apiVersion,
                                           @ApiParam(name = "versionCode", value = "标准版本代码")
                                           @RequestParam(value = "versionCode") String versionCode,
                                           @ApiParam(name = "dictId", value = "字典ID")
                                           @RequestParam(value = "dictId") String dictId,
                                           @ApiParam(name = "entryCode", value = "字典项代码")
                                           @RequestParam(value = "entryCode") String entryCode,
                                           @ApiParam(name = "entryValue", value = "字典项值")
                                           @RequestParam(value = "entryValue") String entryValue,
                                           @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                           @RequestParam(value = "page") int page,
                                           @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                           @RequestParam(value = "rows") int rows) {
        return null;
    }


}
