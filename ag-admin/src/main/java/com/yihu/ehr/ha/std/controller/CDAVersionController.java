package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/version")
@RestController
public class CDAVersionController {

    @RequestMapping(value = "isLatestVersion", method = RequestMethod.GET)
    public String isLatestVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "versionCode", value = "版本代码")
                                  @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/latestVersion",method = RequestMethod.GET)
    public Object getLatestVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion) {
        return null;
    }

    @RequestMapping(value = "/addVersion",method = RequestMethod.POST)
    public String addVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "apiVersion") String apiVersion,
                             @ApiParam(name = "latestVersion", value = "父级版本号")
                             @RequestParam(value = "latestVersion") String latestVersion,
                             @ApiParam(name = "versionName", value = "版本名称")
                             @RequestParam(value = "versionName") String versionName,
                             @ApiParam(name = "userId", value = "用户ID")
                             @RequestParam(value = "userId") String userId) {
        return null;
    }

    @RequestMapping(value = "/versions",method = RequestMethod.GET)
    public String getVersionsByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                          @PathVariable(value = "apiVersion") String apiVersion,
                                          @ApiParam(name = "code", value = "版本代码")
                                          @RequestParam(value = "code") String code,
                                          @ApiParam(name = "name", value = "版本名称")
                                          @RequestParam(value = "name") String name, @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                          @RequestParam(value = "page") int page,
                                          @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                          @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/dropCDAVersion", method = RequestMethod.POST)
    public String dropCDAVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "versionCode", value = "版本代码")
                                 @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    //发布新版本
    @RequestMapping(value = "/commitVersion",method = RequestMethod.POST)
    public String commitVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "versionCode", value = "版本代码")
                                @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/existInStage",method = RequestMethod.GET)
    public String existInStage(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion){
        return null;
    }

    @RequestMapping(value = "/allVersions",method = RequestMethod.GET)
    public Object getAllVersions(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion)
    {
        return null;
    }

}
