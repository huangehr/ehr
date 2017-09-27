package com.yihu.ehr.organization.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(name=MicroServices.Organization)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrganizationClient {


    @RequestMapping(value = "/organizations/getAllOrgs", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有的机构列表")
    List getAllOrgs();


    @RequestMapping(value = "/organizations/list", method = RequestMethod.POST)
    @ApiOperation(value = "根据条件查询机构列表")
    ResponseEntity<List<MOrganization>> searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestBody(required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
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
    @RequestMapping(value = "/organizations" , method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建机构")
    MOrganization create(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgModelJsonData ) ;



    @RequestMapping(value = "/organizations" , method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改机构")
    MOrganization update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgModelJsonData ) ;


    /**
     * 根据机构代码获取机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    MOrganization getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) ;

    /**
     * 根据机构ID获取机构
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/organizations/getOrgById/{org_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构ID获取机构")
    MOrganization getOrgById(
            @ApiParam(name = "org_id", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_id") String orgId) ;


    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/organizations/name", method = RequestMethod.GET)
    List<String> getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name);


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
     * @param district
     * @return
     */
    @RequestMapping(value = "/organizations/geography" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    Collection<MOrganization> getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "市")
            @RequestParam(value = "district") String district);

    @RequestMapping( value = "/organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    Map<String, String> distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) ;

    @RequestMapping(value = "/organizations/existence/{org_code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    boolean isOrgCodeExists(
            @ApiParam(name = "org_code", value = "org_code", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = "/organizations/checkSunOrg" , method = RequestMethod.PUT)
    @ApiOperation(value = "判断机构是否已经是子机构")
    boolean checkSunOrg(
            @ApiParam(name = "org_pId", value = "org_pId", defaultValue = "")
            @RequestParam(value = "org_pId") String orgPid,
            @ApiParam(name = "org_id", value = "org_id", defaultValue = "")
            @RequestParam(value = "org_id") String orgId);

    @RequestMapping(value = "/organizations/images" , method = RequestMethod.POST)
    @ApiOperation(value = "机构资质图片上传")
    public String uploadPicture(
            @ApiParam(name = "jsonData", value = "jsonData", defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = "/organizations/images",method = RequestMethod.GET)
    @ApiOperation(value = "头像下载")
    String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName);

    @RequestMapping(value = "/orgDept/getAllOrgAndDepts", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有的机构列表")
    List<Object> getOrgs();

    @RequestMapping(value = "/organizations/getAllSaasOrgs", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有的机构列表")
    List getAllSaasOrgs(
            @ApiParam(name = "saasName", value = "名称", defaultValue = "")
            @RequestParam(value = "saasName", required = false) String saasName);

    /**
     * 根据机构ID获取机构
     * @param userOrgCode
     * @return
     */
    @RequestMapping(value = "/organizations/getOrgListById", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构ID获取机构")
    List<String> getOrgListById(
            @ApiParam(name = "userOrgCode", value = "用户所在机构", defaultValue = "")
            @RequestParam(value = "userOrgCode", required = false) List<Long> userOrgCode) ;

}
