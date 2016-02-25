package com.yihu.ehr.ha.organization.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-org")
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrganizationClient {

    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    List<MOrganization> searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) ;


    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据机构代码删除机构")
    boolean deleteOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) ;


    /**
     * 创建机构
     * @param orgModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构")
    Object create(String orgModelJsonData ) ;

    @RequestMapping(value = "/organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    Object update(String orgModelJsonData ) ;


    /**
     * 根据机构代码获取机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    MOrganization getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @RequestParam(value = "org_code") String orgCode) ;


    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/organizations/{name}", method = RequestMethod.GET)
    List<String> getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @PathVariable(value = "name") String name);


    /**
     * 跟新机构激活状态
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}/{activity_flag}" , method = RequestMethod.PUT)
    @ApiOperation(value = "跟新机构激活状态")
    boolean activity(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") int activityFlag) ;


    /**
     * 根据地址获取机构下拉列表
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/organizations/{province}/{city}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    Collection<MOrganization> getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @PathVariable(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @PathVariable(value = "city") String city) ;

    @RequestMapping( value = "/organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    Map<String, String> distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) ;
}
