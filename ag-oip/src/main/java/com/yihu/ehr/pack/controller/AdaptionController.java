package com.yihu.ehr.pack.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.ObjectId;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * 标准与适配下发控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/adapter-dispatcher")
public class AdaptionController {
    @RequestMapping(value = "/versionplan", method = {RequestMethod.GET})
    @ApiOperation(value = "获取标准最新版本", produces = "application/text")
    public Object getLatestVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "demographic_id", value = "用户名")
            @RequestParam(value = "demographic_id", required = true) String demographicId) {
        return null;
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.GET})
    @ApiOperation(value = "获取机构数据标准", produces = "application/gzip")
    public Object getSchema(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        return null;
    }

    @RequestMapping(value = "/schemaMappingPlan", method = {RequestMethod.GET})
    @ApiOperation(value = "获取机构适配方案", produces = "application/gzip")
    public Object getAdaption(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion
    ) {
        return null;
    }

    @RequestMapping(value = "/allSchemaMappingPlan", method = {RequestMethod.GET})
    @ApiOperation(value = "获取所有适配方案", produces = "application/gzip")
    public Object getAllAdaption(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        return null;
    }
}
