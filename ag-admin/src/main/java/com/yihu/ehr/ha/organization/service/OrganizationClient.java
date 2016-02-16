package com.yihu.ehr.ha.organization.service;

import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-org")
public interface OrganizationClient {

    @RequestMapping(value = "/rest/v1.0/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    Object searchOrgs(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "fullName", value = "全称")
            @RequestParam(value = "fullName") String fullName,
            @ApiParam(name = "settledWay", value = "接入方式")
            @RequestParam(value = "settledWay",required = false) String settledWay,
            @ApiParam(name = "orgType", value = "机构类型")
            @RequestParam(value = "orgType",required = false) String orgType,
            @ApiParam(name = "province", value = "省、自治区、直辖市")
            @RequestParam(value = "province",required = false) String province,
            @ApiParam(name = "city", value = "市、自治州、自治县、县")
            @RequestParam(value = "city",required = false) String city,
            @ApiParam(name = "district", value = "区/县")
            @RequestParam(value = "district",required = false) String district,
            @ApiParam(name = "page", value = "页面号，从0开始", defaultValue = "0")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "页面记录数", defaultValue = "10")
            @RequestParam(value = "rows") int rows);

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/rest/v1.0/organizations/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根基机构代码删除机构")
    Object deleteOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode);


    /**
     * 创建机构
     * @param apiVersion
     * @param orgModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/organizations" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构")
    Object create(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String orgModelJsonData );

    @RequestMapping(value = "/rest/v1.0/organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    Object update(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String orgModelJsonData ) ;


    /**
     * 根据机构代码获取机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/rest/v1.0/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    MOrganization getOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @RequestParam(value = "org_code") String orgCode);


    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/rest/v1.0/organizations/{name}", method = RequestMethod.GET)
    Object getIdsByName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @PathVariable(value = "name") String name) ;


    /**
     * 跟新机构激活状态
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/rest/v1.0/organizations/{org_code}/{activity_flag}" , method = RequestMethod.PUT)
    @ApiOperation(value = "跟新机构激活状态")
    boolean activity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") int activityFlag);


    /**
     * 根据地址获取机构下拉列表
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/rest/v1.0/organizations/{province}/{city}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    Map<String, Object> getOrgsByAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省")
            @PathVariable(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @PathVariable(value = "city") String city) ;

    @RequestMapping( value = "/rest/v1.0/organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode);
}
