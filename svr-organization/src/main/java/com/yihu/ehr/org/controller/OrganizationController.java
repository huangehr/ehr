package com.yihu.ehr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.org.feign.SecurityClient;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.org.service.Organization;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping(ApiVersion.Version1_0 )
@Api(protocols = "https", value = "org", description = "组织机构管理接口", tags = {"机构管理"})
public class OrganizationController extends BaseRestController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private SecurityClient securityClient;


    /**
     * 机构列表查询
     * @param fields
     * @param filters
     * @param sorts
     * @param size
     * @param page
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    public List<MOrganization> searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        List<Organization> organizationList = orgService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgService.getCount(filters), page, page);
        return (List<MOrganization>)convertToModels(organizationList, new ArrayList<MOrganization>(organizationList.size()), MOrganization.class, fields);
    }


    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据机构代码删除机构")
    public boolean deleteOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception{
        orgService.delete(orgCode);
        return true;
    }


    /**
     * 创建机构
     * @param orgJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构")
    public MOrganization create(String orgJsonData ) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Organization org = objectMapper.readValue(orgJsonData, Organization.class);
        if(orgService.isExistOrg(org.getOrgCode())) throw new ApiException(ErrorCode.ExistOrgForCreate, "该机构已存在");
        org.setActivityFlag(1);
        orgService.save(org);
        return convertToModel(org,MOrganization.class);
    }

    @RequestMapping(value = "organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    public MOrganization update(
            String jsonData ) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Organization org = objectMapper.readValue(jsonData, Organization.class);
        if(orgService.isExistOrg(org.getOrgCode())) throw new ApiException(ErrorCode.ExistOrgForCreate, "该机构已存在");
        orgService.save(org);
        return convertToModel(org,MOrganization.class);
    }


    /**
     * 根据机构代码获取机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    public MOrganization getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception{
        Organization org = orgService.getOrg(orgCode);
        MOrganization orgModel = convertToModel(org,MOrganization.class);
        return orgModel;
    }


    /**
     * 根据name获取机构orgCodes
     * @param name
     * @return
     */
    @ApiOperation(value = "根据名称获取机构编号列表")
    @RequestMapping(value = "/organizations/name", method = RequestMethod.GET)
    public List<String> getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        List<String> orgCodes = orgService.getCodesByName(name);
        return orgCodes;
    }


    /**
     * 跟新机构激活状态
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "organizations/{org_code}/{activity_flag}" , method = RequestMethod.PUT)
    @ApiOperation(value = "跟新机构激活状态")
    public boolean activity(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") int activityFlag) throws Exception{
        Organization org = orgService.getOrg(orgCode);
        if("1".equals(activityFlag)){
            org.setActivityFlag(0);
        }else{
            org.setActivityFlag(1);
        }
        orgService.save(org);
        return true;
    }

    /**
     * 根据地址获取机构下拉列表
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "organizations/geography" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    public List<MOrganization> getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "市")
            @RequestParam(value = "district") String district) {
        List<Organization> orgList = orgService.searchByAddress(province,city,district);
        return (List<MOrganization>)convertToModels(orgList, new ArrayList<MOrganization>(orgList.size()), MOrganization.class,null);
    }

    @RequestMapping( value = "organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        MUserSecurity userSecurity = securityClient.getUserSecurityByOrgCode(orgCode);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity == null) {
            userSecurity = securityClient.createSecurityByOrgCode(orgCode);
        }else{
            //result.setErrorMsg("公钥信息已存在。");
            //这里删除原有的公私钥重新分配
            String userKeyId = securityClient.getUserKeyIdByOrgCd(orgCode);
            securityClient.deleteSecurity(userSecurity.getId());
            securityClient.deleteUserKey(userKeyId);
            userSecurity = securityClient.createSecurityByOrgCode(orgCode);
        }
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
        return keyMap;
    }


}
