package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/cdaType")
@RestController
public class CDATypeController {

    @RequestMapping(value = "/getTreeGridData",method = RequestMethod.GET)
    public String getTreeGridData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "apiVersion") String apiVersion) {
        return null;
    }

    @RequestMapping(value = "/cdaType", method = RequestMethod.GET)
    public Object getCdaTypeById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "typeId", value = "类别ID")
                                 @RequestParam(value = "typeId") String typeId) {
        return null;
    }

    @RequestMapping(value = "cdaType",method = RequestMethod.DELETE)
    public Object delteCdaTypeInfo(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion,
                                   @ApiParam(name = "typeId", value = "类别ID")
                                   @RequestParam(value = "typeId") String typeId) {
        return null;
    }

    @RequestMapping(value = "SaveCdaType",method = RequestMethod.POST)
    public Object SaveCdaType(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "apiVersion") String apiVersion,
                              @ApiParam(name = "typeJson", value = "类别信息Json")
                              @RequestParam(value = "typeJson") String typeJson) {
        return null;
    }

    @RequestMapping(value = "getParentType",method = RequestMethod.GET)
    public String getParentType(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name="typeId",value = "类别ID")
                                @RequestParam(value = "typeId")String typeId,
                                @ApiParam(name="code",value = "类别代码")
                                @RequestParam(value = "code")String code,
                                @ApiParam(name="name",value = "类别名称")
                                @RequestParam(value = "name")String name) {
        return  null;
    }
    @RequestMapping(value = "getCDATypeListByParentId",method = RequestMethod.GET)
    public String getCDATypeListByParentId(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                           @PathVariable(value = "apiVersion") String apiVersion,
                                           @ApiParam(name = "typeId", value = "类别ID")
                                           @RequestParam(value = "typeId") String typeId) {
        return null;
    }
}
