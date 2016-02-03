package com.yihu.ehr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.org.feign.SecurityClient;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.org.service.OrgModel;
import com.yihu.ehr.org.service.Organization;
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
    private SecurityClient securityClient;


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

        List<MOrganization> organizationList = orgManagerService.searchOrgDetailModel(apiVersion,conditionMap);
        Integer totalCount = orgManagerService.searchCount(apiVersion,conditionMap);
        Map<String,Object> map = new HashMap<>();
        map.put("organizationList",organizationList);
        map.put("totalCount",totalCount);
        return map;

    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Object deleteOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) throws Exception{
        orgManagerService.delete(orgCode);
        return true;
    }


    /**
     * 激活机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/{activity_flag}" , method = RequestMethod.PUT)
    public boolean activity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") String activityFlag) throws Exception{
        Organization org = orgManagerService.getOrg(orgCode);
        if("1".equals(activityFlag)){
            org.setActivityFlag(0);
            orgManagerService.update(org);
        }else{
            org.setActivityFlag(1);
            orgManagerService.update(org);
        }
        return true;
    }


    /**
     * 跟新机构
     * @param apiVersion
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "" , method = RequestMethod.PUT)
    public Object updateOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String orgModelJsonData ) throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();
        OrgModel orgModel = objectMapper.readValue(orgModelJsonData, OrgModel.class);

        if (orgModel.getUpdateFlg().equals("0")){
            if(orgManagerService.isExistOrg(orgModel.getOrgCode())){
                return false;
            }
            Organization org = orgManagerService.register(orgModel.getOrgCode(), orgModel.getFullName(), orgModel.getShortName());
            org.setActivityFlag(1);
            MAddress location =  new MAddress();
            location.setCity(orgModel.getCity());
            location.setDistrict(orgModel.getDistrict());
            location.setProvince(orgModel.getProvince());
            location.setTown(orgModel.getTown());
            location.setStreet(orgModel.getStreet());
            orgManagerService.saveAddress(apiVersion,location);

            org.setAdmin(orgModel.getAdmin());
            org.setTel(orgModel.getTel());
            org.setOrgType(orgModel.getOrgType());
            org.setSettledWay(orgModel.getSettledWay());
            org.setOrgType(orgModel.getOrgType());
            org.setSettledWay(orgModel.getSettledWay());
            org.addTag(orgModel.getTags());
            orgManagerService.update(org);
            return true;
        }else{
            Organization org = null;
            if (orgModel.getUpdateFlg().equals("0")) {
                org = orgManagerService.register(orgModel.getOrgCode(), orgModel.getFullName(), orgModel.getShortName());
            } else if (orgModel.getUpdateFlg().equals("1")) {
                org = orgManagerService.getOrg(orgModel.getOrgCode());
            }
            if (org == null) {
                return false;
            }
            org.setSettledWay(orgModel.getSettledWay());
            org.setOrgType(orgModel.getOrgType());
            org.setShortName(orgModel.getShortName());
            org.setFullName(orgModel.getFullName());
            org.setAdmin(orgModel.getAdmin());
            org.setTel(orgModel.getTel());
            org.setTags("");
            org.addTag(orgModel.getTags());
            MAddress location = new MAddress();
            location.setProvince(orgModel.getProvince());
            location.setCity(orgModel.getCity());
            location.setDistrict(orgModel.getDistrict());
            location.setTown(orgModel.getTown());
            orgManagerService.saveAddress(apiVersion,location);
            orgManagerService.update(org);
            return true;
        }
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getOrgByCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @RequestParam(value = "org_code") String orgCode) {
        Organization organization = orgManagerService.getOrg(orgCode);
        return convertToModel(organization,MOrganization.class);
    }

    /**
     * 根据code获取机构信息
     * @param orgCode
     * @return
     */
    @ApiOperation(value = "根据地址代码获取model")
    @RequestMapping(value = "/org_model", method = RequestMethod.GET)
    public Object getOrgModel(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {
        OrgModel orgModel = new OrgModel();
        Organization org = orgManagerService.getOrg(orgCode);
        if(org!=null){
            orgModel = orgManagerService.getOrgModel(apiVersion,org);
        }
        return orgModel;
    }

    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public Object getIdsByName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @PathVariable(value = "name") String name) {
        OrgModel orgModel = new OrgModel();
        List<String> ids = orgManagerService.getIdsByName(name);
        return ids;
    }



    /**
     * 根据地址获取机构
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/search/{province}/{city}" , method = RequestMethod.GET)
    public Object getOrgsByAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省")
            @PathVariable(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @PathVariable(value = "city") String city) {

        List<Organization> orgList = orgManagerService.searchByAddress(apiVersion,province,city);
        Map<String, String> orgMap = new HashMap<>();
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

    @RequestMapping(value = "/validation" ,method = RequestMethod.GET)
    @ResponseBody
    public Object validationOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode){
        Organization org = orgManagerService.getOrg(orgCode);
        if(org == null){
            return true;
        }else{
            return false;
        }

    }

}
