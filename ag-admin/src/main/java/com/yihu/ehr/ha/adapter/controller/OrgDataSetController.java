package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/27.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/orgDataSet")
@RestController
public class OrgDataSetController   {

    @RequestMapping(value = "/orgDataSet",method = RequestMethod.GET)
    public Object getOrgDataSetById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "id",value = "数据集ID")
                                    @RequestParam(value = "id")String id){
        return null;
    }

    @RequestMapping(value = "/orgMetaData",method = RequestMethod.GET)
    public Object getOrgMetaDataById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "api_version") String apiVersion,
                                     @ApiParam(name = "metaDataId", value = "机构数据元ID")
                                     @RequestParam(value = "metaDataId") String metaDataId) {
        return null;
    }

    @RequestMapping(value = "/orgDataSet", method = RequestMethod.POST)
    public String createOrgDataSet(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
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

    @RequestMapping(value = "/updateOrgDataSet",method = RequestMethod.POST)
    public String updateOrgDataSet(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "api_version") String apiVersion,
                                   @ApiParam(name = "id", value = "数据集ID")
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

    @RequestMapping(value = "/deleteOrgDataSet",method = RequestMethod.DELETE)
    public String deleteOrgDataSet(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "api_version") String apiVersion,
                                   @ApiParam(name = "id", value = "数据集ID")
                                   @RequestParam(value = "id") long id) {

        return null;
    }

    @RequestMapping(value = "/orgDataSets",method = RequestMethod.GET)
    public String getOrgDataSetsByCodeOrName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
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

    @RequestMapping(value = "/createOrgMetaData", method = RequestMethod.POST)
    public String createOrgMetaData(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "orgDataSetSeq", value = "数据集序号")
                                    @RequestParam(value = "orgDataSetSeq") int orgDataSetSeq,
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

    @RequestMapping(value = "/updateOrgMetaData", method = RequestMethod.POST)
    public String updateOrgMetaData(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                        @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "orgDataSetSeq", value = "数据集序号")
                                        @RequestParam(value = "orgDataSetSeq") int orgDataSetSeq,
                                    @ApiParam(name = "metaDataId",value = "数据元ID")
                                    @RequestParam(value = "metaDataId")long metaDataId,
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

    @RequestMapping(value = "/deleteOrgMetaData",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据元", produces = "application/json", notes = "删除机构数据元信息，批量删除时，Id以逗号隔开")
    public String deleteOrgMetaData(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "ids", value = "数据元ID")
                                    @RequestParam(value = "ids") String ids) {
        return null;
    }

    @RequestMapping(value = "/orgMetaDatas",method = RequestMethod.GET)
    public String getOrgMetaDatasByCodeOrName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                              @PathVariable(value = "api_version") String apiVersion,
                                              @ApiParam(name = "orgCode", value = "机构代码")
                                              @RequestParam(value = "orgCode") String orgCode,
                                              @ApiParam(name = "orgDataSetSeq", value = "数据集序号")
                                              @RequestParam(value = "orgDataSetSeq") int orgDataSetSeq,
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
