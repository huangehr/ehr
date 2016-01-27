package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterDispatch")
@RestController
public class AdapterDispatchRestController extends BaseRestController {

    @RequestMapping(value = "/schemaMappingPlan", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案映射信息", produces = "application/json", notes = "获取采集标准适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getSchemeMappingInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgcode) {
        return null;
    }

    @RequestMapping(value = "/allSchemaMappingPlan", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集标准及适配方案信息", produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getALLSchemeMappingInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgcode) {

        return null;
    }

    @RequestMapping(value = "/versionplan", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号 ",  produces = "application/json", notes = "指定版本的信息")
    public Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) {
        return null;
    }
}
