package com.yihu.ehr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.model.address.MGeography;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.org.feign.ConventionalDictClient;
import com.yihu.ehr.org.feign.GeographyClient;
import com.yihu.ehr.org.feign.SecurityClient;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.org.service.Organization;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 )
@Api(protocols = "https", value = "org", description = "组织机构管理接口", tags = {"机构管理"})
public class OrganizationController extends BaseRestController {

    @Autowired
    private OrgService orgManagerService;

    @Autowired
    GeographyClient addressClient;

    @Autowired
    SecurityClient securityClient;

    @Autowired
    private ConventionalDictClient conventionalDictClient;



    /**
     * 机构列表查询
     * @param settledWay
     * @param orgType
     * @param province
     * @param city
     * @param district
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    public Object searchOrgs(
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
            @RequestParam(value = "rows") int rows) throws Exception{

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("orgCode", orgCode);
        conditionMap.put("fullName", fullName);
        conditionMap.put("settledWay", settledWay);
        conditionMap.put("orgType", orgType);
        conditionMap.put("province", province);
        conditionMap.put("city", city);
        conditionMap.put("district", district);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        List<Organization> organizationList = orgManagerService.search(apiVersion,conditionMap);
        List<MOrganization> organizationModels = new ArrayList<>();
        for(Organization organization:organizationList){
            MOrganization organizationModel = convertToModel(organization,MOrganization.class);
            organizationModels.add(organizationModel);
        }
        Integer totalCount = orgManagerService.searchCount(apiVersion,conditionMap);
        return getResult(organizationModels,totalCount,page,rows);

    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根基机构代码删除机构")
    public Object deleteOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception{
        orgManagerService.delete(orgCode);
        return true;
    }


    /**
     * 创建机构
     * @param apiVersion
     * @param orgModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构")
    public Object create(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String orgModelJsonData ) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MOrganization orgModel = objectMapper.readValue(orgModelJsonData, MOrganization.class);
        if(orgManagerService.isExistOrg(orgModel.getOrgCode())){
            return new ApiErrorEcho(ErrorCode.ExistOrgForCreate, "该机构已存在");
        }
        Organization org = convertToModel(orgModel,Organization.class);
        org.setActivityFlag(1);
        MGeography location =  orgModel.getLocation();
        orgManagerService.saveAddress(apiVersion,location);
        orgManagerService.save(org);
        return org;
    }

    @RequestMapping(value = "organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    public Object update(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String orgModelJsonData ) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MOrganization OrganizationModel = objectMapper.readValue(orgModelJsonData, MOrganization.class);
        if(orgManagerService.isExistOrg(OrganizationModel.getOrgCode())){
            return new ApiErrorEcho(ErrorCode.ExistOrgForCreate, "该机构已存在");
        }
        Organization org = convertToModel(OrganizationModel,Organization.class);
        MGeography location =  OrganizationModel.getLocation();
        orgManagerService.saveAddress(apiVersion,location);
        orgManagerService.save(org);
        return true;
    }


    /**
     * 根据机构代码获取机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    public MOrganization getOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @RequestParam(value = "org_code") String orgCode) throws Exception{
        Organization org = orgManagerService.getOrg(orgCode);
        MOrganization orgModel = convertToModel(org,MOrganization.class);
        orgModel.setOrgType(conventionalDictClient.getOrgType(apiVersion,org.getOrgType()));
        orgModel.setSettledWay(conventionalDictClient.getSettledWay(apiVersion,org.getSettledWay()));
        orgModel.setLocation(addressClient.getAddressById(apiVersion,org.getLocation()));
        return orgModel;
    }


    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/organizations/{name}", method = RequestMethod.GET)
    public Object getIdsByName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @PathVariable(value = "name") String name) {
        List<String> ids = orgManagerService.getIdsByName(name);
        return ids;
    }


    /**
     * 跟新机构激活状态
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "organizations/{org_code}/{activity_flag}" , method = RequestMethod.PUT)
    @ApiOperation(value = "跟新机构激活状态")
    public boolean activity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") int activityFlag) throws Exception{
        Organization org = orgManagerService.getOrg(orgCode);
        if("1".equals(activityFlag)){
            org.setActivityFlag(0);
        }else{
            org.setActivityFlag(1);
        }
        orgManagerService.save(org);
        return true;
    }


    /**
     * 根据地址获取机构下拉列表
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "organizations/{province}/{city}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    public Map<String, Object> getOrgsByAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省")
            @PathVariable(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @PathVariable(value = "city") String city) {

        List<Organization> orgList = orgManagerService.searchByAddress(apiVersion,province,city);
        Map<String, Object> orgMap = new HashMap<>();
        for (Organization org : orgList) {
            orgMap.put(org.getOrgCode(), org.getFullName());
        }
        return orgMap;
    }

    @RequestMapping( value = "organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        MUserSecurity userSecurity = securityClient.getUserSecurityByOrgCode(apiVersion,orgCode);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity == null) {
            userSecurity = securityClient.createSecurityByOrgCode(apiVersion,orgCode);
        }else{
            //result.setErrorMsg("公钥信息已存在。");
            //这里删除原有的公私钥重新分配
            String userKeyId = securityClient.getUserKeyIdByOrgCd(apiVersion,orgCode);
            securityClient.deleteSecurity(apiVersion,userSecurity.getId());
            securityClient.deleteUserKey(apiVersion,userKeyId);
            userSecurity = securityClient.createSecurityByOrgCode(apiVersion,orgCode);
        }

        //String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");

        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
        return keyMap;
    }


}
