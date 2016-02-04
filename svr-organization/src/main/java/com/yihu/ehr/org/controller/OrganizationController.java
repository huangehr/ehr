package com.yihu.ehr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.model.address.MAddress;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/org")
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
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public Object searchOrgs(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "fullName", value = "全称")
            @RequestParam(value = "fullName") String fullName,
            @ApiParam(name = "settledWay", value = "接入方式",defaultValue = "")
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

        List<MOrganization> organizationList = orgManagerService.search(apiVersion,conditionMap);
        Integer totalCount = orgManagerService.searchCount(apiVersion,conditionMap);
        return getResult(organizationList,totalCount,page,rows);

    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/{org_code}", method = RequestMethod.DELETE)
    public Object deleteOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception{
        orgManagerService.delete(orgCode);
        return true;
    }




    @RequestMapping(value = "" , method = RequestMethod.POST)
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
        MAddress location =  orgModel.getLocation();
        orgManagerService.saveAddress(apiVersion,location);
        orgManagerService.save(org);
        return org;
    }

    @RequestMapping(value = "" , method = RequestMethod.PUT)
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
        MAddress location =  OrganizationModel.getLocation();
        orgManagerService.saveAddress(apiVersion,location);
        orgManagerService.save(org);
        return true;
    }


    /**
     * 根据code获取机构信息
     * @param orgCode
     * @return
     */
    @ApiOperation(value = "根据地址代码获取机构")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object getOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {

        Organization org = orgManagerService.getOrg(orgCode);
        MOrganization orgModel = convertToModel(org,MOrganization.class);
        orgModel.setOrgType(conventionalDictClient.getOrgType(apiVersion,org.getOrgType()));
        orgModel.setSettledWay(conventionalDictClient.getSettledWay(apiVersion,org.getSettledWay()));
        orgModel.setLocation(addressClient.getAddressById(apiVersion,org.getLocation()));
        return orgModel;
    }


    /**
     * 激活机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/{org_code}/{activity_flag}" , method = RequestMethod.PUT)
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
     * 根据地址获取机构
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/search/{province}/{city}" , method = RequestMethod.GET)
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

    @RequestMapping( value = "/distributeKey" , method = RequestMethod.POST)
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode) {
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
