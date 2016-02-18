package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/27.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/plan")
@RestController
public class OrgAdapterPlanController   {

    @RequestMapping(value = "/adapterPlans", method = RequestMethod.GET)
    public String getAdapterPlanByCodeOrName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                             @PathVariable(value = "api_version") String apiVersion,
                                             @ApiParam(name = "code", value = "代码")
                                             @RequestParam(value = "code") String code,
                                             @ApiParam(name = "name", value = "名称")
                                             @RequestParam(value = "name") String name,
                                             @ApiParam(name = "type", value = "类型")
                                             @RequestParam(value = "type") String type,
                                             @ApiParam(name = "orgCode", value = "机构代码")
                                             @RequestParam(value = "orgCode") String orgCode,
                                             @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                             @RequestParam(value = "page") int page,
                                             @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                             @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/adapterPlan", method = RequestMethod.GET)
    public Object getAdapterPlanById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "api_version") String apiVersion,
                                     @ApiParam(name = "id", value = "方案ID")
                                     @RequestParam(value = "id") String id) {
        return null;
    }

    @RequestMapping(value = "/updateAdapterPlan", method = RequestMethod.POST)
    public String updateAdapterPlan(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "id", value = "方案ID")
                                    @RequestParam(value = "id") String id,
                                    @ApiParam(name = "code", value = "方案代码")
                                    @RequestParam(value = "code") String code,
                                    @ApiParam(name = "name", value = "方案名称")
                                    @RequestParam(value = "name") String name,
                                    @ApiParam(name = "description", value = "描述")
                                    @RequestParam(value = "description") String description,
                                    @ApiParam(name = "versionCode", value = "版本号")
                                    @RequestParam(value = "versionCode") String versionCode,
                                    @ApiParam(name = "orgCode", value = "机构代码")
                                    @RequestParam(value = "orgCode") String orgCode,
                                    @ApiParam(name = "parentId", value = "继承标准ID")
                                    @RequestParam(value = "parentId") String parentId,
                                    @ApiParam(name = "isCover", value = "是否覆盖")
                                    @RequestParam(value = "isCover") String isCover) {
        return null;
    }

    @RequestMapping(value = "/orgIsExistData", method = RequestMethod.GET)
    public String orgIsExistData(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "orgCode", value = "机构代码")
                                 @RequestParam(value = "orgCode") String orgCode) {
        return null;
    }

    @RequestMapping(value = "/delAdapterPlan", method = RequestMethod.GET)
    @ApiOperation(value = "删除适配方案", produces = "application/json", notes = "删除适配方案信息，批量删除时，Id以逗号隔开")
    public String delAdapterPlan(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "ids", value = "方案ID")
                                 @RequestParam("ids") String ids) {
        return null;
    }

    @RequestMapping(value = "/getAdapterPlanList", method = RequestMethod.GET)
    public String getAdapterPlanList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "api_version") String apiVersion,
                                     @ApiParam(name = "type", value = "类型")
                                     @RequestParam(value = "type") String type,
                                     @ApiParam(name = "versionCode", value = "版本号")
                                     @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/getAdapterOrgList",method = RequestMethod.GET)
    public String getAdapterOrgList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "type", value = "类型")
                                    @RequestParam(value = "type") String type) {
        return null;
    }


    @RequestMapping(value = "/adapterDataSet",method = RequestMethod.GET)
    public String adapterDataSet(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "planId", value = "方案ID")
                                 @RequestParam(value = "planId") long planId,
                                 @ApiParam(name = "customizeData", value = "")
                                 @RequestParam(value = "customizeData") String customizeData) {
        return null;
    }

    @RequestMapping(value = "/getAdapterCustomize",method = RequestMethod.GET)
    public Object getAdapterCustomize(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "versionCode", value = "版本号")
                                 @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }
}
