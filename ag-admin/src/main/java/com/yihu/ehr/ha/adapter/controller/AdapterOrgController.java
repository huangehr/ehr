package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/adapterOrg")
@RestController
public class AdapterOrgController {

    @RequestMapping(value = "/adapterOrg",method = RequestMethod.GET)
    public Object getAdapterOrgByCode(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                      @ApiParam(name = "code", value = "代码")
                                      @RequestParam(value = "code") String code) {
        return null;
    }

    @RequestMapping(value = "/adapterOrgs", method = RequestMethod.GET)
    public String getAdapterOrgByCodeOrName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                            @PathVariable(value = "api_version") String apiVersion,
                                            @ApiParam(name = "", value = "机构代码")
                                            @RequestParam(value = "code") String code,
                                            @ApiParam(name = "name", value = "机构名称")
                                            @RequestParam(value = "name") String name,
                                            @ApiParam(name = "type", value = "类型")
                                            @RequestParam(value = "type") String type,
                                            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                            @RequestParam(value = "page") int page,
                                            @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                            @RequestParam(value = "rows") int rows) {
        return null;
    }


    @RequestMapping(value = "/addAdapterOrg",method = RequestMethod.POST)
    public String addAdapterOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "type", value = "类别")
                                @RequestParam(value = "type") String type,
                                @ApiParam(name = "orgCode", value = "机构代码")
                                @RequestParam(value = "orgCode") String orgCode,
                                @ApiParam(name = "orgName", value = "机构名称")
                                @RequestParam(value = "orgName") String orgName,
                                @ApiParam(name = "code", value = "代码")
                                @RequestParam(value = "code") String code,
                                @ApiParam(name = "name", value = "名称")
                                @RequestParam(value = "name") String name,
                                @ApiParam(name = "parent", value = "父级")
                                @RequestParam(value = "parent") String parent,
                                @ApiParam(name = "description", value = "描述")
                                @RequestParam(value = "description") String description) {
        return null;
    }

    @RequestMapping(value = "/updateAdapterOrg",method = RequestMethod.POST)
    public String updateAdapterOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "api_version") String apiVersion,
                                   @ApiParam(name = "type", value = "类别")
                                   @RequestParam(value = "type") String type,
                                   @ApiParam(name = "orgCode", value = "机构代码")
                                   @RequestParam(value = "orgCode") String orgCode,
                                   @ApiParam(name = "orgName", value = "机构名称")
                                   @RequestParam(value = "orgName") String orgName,
                                   @ApiParam(name = "code", value = "代码")
                                   @RequestParam(value = "code") String code,
                                   @ApiParam(name = "name", value = "名称")
                                   @RequestParam(value = "name") String name,
                                   @ApiParam(name = "parent", value = "父级")
                                   @RequestParam(value = "parent") String parent,
                                   @ApiParam(name = "description", value = "描述")
                                   @RequestParam(value = "description") String description) {
        return null;
    }

    @RequestMapping(value = "/adapterOrg",method = RequestMethod.DELETE)
    public String delAdapterOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "code", value = "代码")
                                @RequestParam(value = "code") String code) {
        return null;
    }
    @RequestMapping(value = "/getAdapterOrgList",method = RequestMethod.GET)
    public String getAdapterOrgList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "type", value = "类别")
                                    @RequestParam(value = "type") String type) {
        return null;
    }

    @RequestMapping(value = "/searchOrgList",method = RequestMethod.GET)
    public String searchOrgList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "type", value = "类别")
                                @RequestParam(value = "type") String type,
                                @ApiParam(name = "orgCode", value = "机构代码")
                                @RequestParam(value = "orgCode") String orgCode,
                                @ApiParam(name = "fullName", value = "全称")
                                @RequestParam(value = "fullName") String fullName,
                                @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                @RequestParam(value = "page") int page,
                                @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                @RequestParam(value = "rows") int rows) {

        return null;
    }

    @RequestMapping(value = "/searchAdapterOrgList",method = RequestMethod.GET)
    public String searchAdapterOrgList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                       @PathVariable(value = "api_version") String apiVersion,
                                       @ApiParam(name = "type", value = "类别")
                                       @RequestParam(value = "type") String type,
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
