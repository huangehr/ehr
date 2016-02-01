package com.yihu.ehr.ha.organization.service;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-org")
public interface OrganizationClient {

    @RequestMapping(value = "/rest/{api_version}/org/search", method = RequestMethod.GET)
    Object searchOrgs(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                      @PathVariable(value = "api_version") String apiVersion,
                      @ApiParam(name = "orgCode", value = "机构代码")
                      @RequestParam(value = "orgCode") String orgCode,
                      @ApiParam(name = "fullName", value = "全称")
                      @RequestParam(value = "fullName") String fullName,
                      @ApiParam(name = "settledWay", value = "接入方式", defaultValue = "")
                      @RequestParam(value = "settledWay") String settledWay,
                      @ApiParam(name = "orgType", value = "机构类型")
                      @RequestParam(value = "orgType") String orgType,
                      @ApiParam(name = "province", value = "省、自治区、直辖市")
                      @RequestParam(value = "province") String province,
                      @ApiParam(name = "city", value = "市、自治州、自治县、县")
                      @RequestParam(value = "city") String city,
                      @ApiParam(name = "district", value = "区/县")
                      @RequestParam(value = "district") String district,
                      @ApiParam(name = "page", value = "页面号，从0开始", defaultValue = "0")
                      @RequestParam(value = "page") int page,
                      @ApiParam(name = "rows", value = "页面记录数", defaultValue = "10")
                      @RequestParam(value = "rows") int rows);

    @RequestMapping(value = "/rest/{api_version}/org/", method = RequestMethod.DELETE)
    Object deleteOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                     @PathVariable(value = "api_version") String apiVersion,
                     @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
                     @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/org/activity", method = RequestMethod.PUT)
    Object activity(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                    @PathVariable(value = "api_version") String apiVersion,
                    @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
                    @RequestParam(value = "orgCode") String orgCode,
                    @ApiParam(name = "activityFlag", value = "状态", defaultValue = "")
                    @RequestParam(value = "activityFlag") String activityFlag);

    @RequestMapping(value = "/rest/{api_version}/org/", method = RequestMethod.PUT)
    Object updateOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                     @PathVariable(value = "api_version") String apiVersion,
                     String orgModelJsonData);

    @RequestMapping(value = "/rest/{api_version}/org/", method = RequestMethod.GET)
    Object getOrgByCode(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                        @PathVariable(value = "api_version") String apiVersion,
                        @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
                        @RequestParam(value = "orgCode") String orgCode);

    @ApiOperation(value = "根据地址代码获取model")
    @RequestMapping(value = "/rest/{api_version}/org/org_model", method = RequestMethod.GET)
    Object getOrgModel(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                       @PathVariable(value = "api_version") String apiVersion,
                       @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
                       @RequestParam(value = "orgCode") String orgCode);

    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/rest/{api_version}/org/name", method = RequestMethod.GET)
    Object getIdsByName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                        @PathVariable(value = "api_version") String apiVersion,
                        @ApiParam(name = "name", value = "机构名称", defaultValue = "")
                        @RequestParam(value = "name") String name);

    @RequestMapping(value = "/rest/{api_version}/org/search", method = RequestMethod.GET)
    Object getOrgsByAddress(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
                            @ApiParam(name = "province", value = "省")
                            @RequestParam(value = "province") String province,
                            @ApiParam(name = "city", value = "市")
                            @RequestParam(value = "city") String city);

    @RequestMapping(value = "/rest/{api_version}/org/distributeKey", method = RequestMethod.POST)
    Object distributeKey(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                         @PathVariable(value = "api_version") String apiVersion,
                         @ApiParam(name = "orgCode", value = "机构代码")
                         @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/org/validation", method = RequestMethod.GET)
    Object validationOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                         @PathVariable(value = "api_version") String apiVersion,
                         @ApiParam(name = "orgCode", value = "机构代码")
                         @RequestParam(value = "orgCode") String orgCode);
}
