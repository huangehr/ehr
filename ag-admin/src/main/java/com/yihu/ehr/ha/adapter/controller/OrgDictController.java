package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/27.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgDict")
@RestController
public class OrgDictController extends BaseRestController {

    @RequestMapping(value = "/orgDict", method = RequestMethod.GET)
    public Object getOrgDictById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "id", value = "字典ID")
                                 @RequestParam(value = "id") String id) {

        return null;
    }

    public Object getOrgDictItemById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "api_version") String apiVersion,
                                     @ApiParam(name = "id", value = "字典项ID")
                                     @RequestParam(value = "id") String id) {
        return null;
    }

    @RequestMapping(value = "/createOrgDict", method = RequestMethod.POST)
    public String createOrgDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "code", value = "代码")
                                @RequestParam(value = "code") String code,
                                @ApiParam(name = "name", value = "名称")
                                @RequestParam(value = "name") String name,
                                @ApiParam(name = "description", value = "描述")
                                @RequestParam(value = "description") String description,
                                @ApiParam(name = "orgCode", value = "机构代码")
                                @RequestParam(value = "orgCode") String orgCode,
                                @ApiParam(name = "userId", value = "用户ID")
                                @RequestParam(value = "userId") String userId) {

        return null;
    }

    @RequestMapping(value = "/updateOrgDict", method = RequestMethod.POST)
    public String updateOrgDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "id", value = "字典ID")
                                @RequestParam(value = "id") long id,
                                @ApiParam(name = "code", value = "代码")
                                @RequestParam(value = "code") String code,
                                @ApiParam(name = "name", value = "名称")
                                @RequestParam(value = "name") String name,
                                @ApiParam(name = "description", value = "描述")
                                @RequestParam(value = "description") String description,
                                @ApiParam(name = "orgCode", value = "机构代码")
                                @RequestParam(value = "orgCode") String orgCode,
                                @ApiParam(name = "userId", value = "用户ID")
                                @RequestParam(value = "userId") String userId) {

        return null;
    }

    @RequestMapping(value = "/deleteOrgDict", method = RequestMethod.DELETE)
    public String deleteOrgDict(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "id", value = "字典ID")
                                @RequestParam(value = "id") long id) {
        return null;
    }

    @RequestMapping(value = "/orgDicts", method = RequestMethod.GET)
    public Object getOrgDictsByCodeOrName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                          @PathVariable(value = "api_version") String apiVersion,
                                          @ApiParam(name = "orgCode", value = "机构代码")
                                          @RequestParam(value = "orgCode") String orgCode,
                                          @ApiParam(name = "code", value = "代码")
                                          @RequestParam(value = "code") String code,
                                          @ApiParam(name = "name", value = "名称")
                                          @RequestParam(value = "name") String name,
                                          @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                          @RequestParam(value = "page") int page,
                                          @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                          @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "createOrgDictItem", produces = "text/html;charset=UTF-8")
    public String createOrgDictItem(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "orgDictSeq", value = "字典序号")
                                    @RequestParam(value = "orgDictSeq") int orgDictSeq,
                                    @ApiParam(name = "orgCode", value = "机构代码")
                                    @RequestParam(value = "orgCode") String orgCode, @ApiParam(name = "code", value = "代码")
                                    @RequestParam(value = "code") String code,
                                    @ApiParam(name = "name", value = "名称")
                                    @RequestParam(value = "name") String name,
                                    @ApiParam(name = "description", value = "描述")
                                    @RequestParam(value = "description") String description,
                                    @ApiParam(name = "sort", value = "排序")
                                    @RequestParam(value = "sort") String sort,
                                    @ApiParam(name = "userId", value = "用户ID")
                                    @RequestParam(value = "userId") String userId) {
        return null;
    }

    @RequestMapping(value="updateDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateDictItem(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "id",value = "字典项ID")
                                 @RequestParam(value = "id")String id,
                                 @ApiParam(name = "orgDictSeq", value = "字典序号")
                                     @RequestParam(value = "orgDictSeq") int orgDictSeq,
                                 @ApiParam(name = "orgCode", value = "机构代码")
                                     @RequestParam(value = "orgCode") String orgCode, @ApiParam(name = "code", value = "代码")
                                     @RequestParam(value = "code") String code,
                                 @ApiParam(name = "name", value = "名称")
                                     @RequestParam(value = "name") String name,
                                 @ApiParam(name = "description", value = "描述")
                                     @RequestParam(value = "description") String description,
                                 @ApiParam(name = "sort", value = "排序")
                                     @RequestParam(value = "sort") String sort,
                                 @ApiParam(name = "userId", value = "用户ID")
                                     @RequestParam(value = "userId") String userId) {
        return null;
    }

        @RequestMapping(value = "/deleteOrgDictItem",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典项", produces = "application/json", notes = "删除机构字典项信息，批量删除时，Id以逗号隔开")
    public String deleteOrgDictItem(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                        @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "ids", value = "字典ID")
                                        @RequestParam(value = "ids") String ids) {
        return null;
    }

    @RequestMapping(value = "/orgDictItems", method = RequestMethod.GET)
    public String getOrgDictItemsByCodeOrName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "api_version") String apiVersion,
                                  @ApiParam(name = "orgCode", value = "机构代码")
                                  @RequestParam(value = "orgCode") String orgCode,
                                  @ApiParam(name = "orgDictSeq", value = "字典序号")
                                  @RequestParam(value = "orgDictSeq") int orgDictSeq,
                                  @ApiParam(name = "code", value = "代码")
                                  @RequestParam(value = "code") String code,
                                  @ApiParam(name = "name", value = "名称")
                                  @RequestParam(value = "name") String name,
                                  @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                  @RequestParam(value = "page") int page,
                                  @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                  @RequestParam(value = "rows") int rows) {
        return null;
    }
}
