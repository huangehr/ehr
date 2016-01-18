package com.yihu.ehr.org.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.org.service.OrgManagerService;
import com.yihu.ehr.org.service.OrgModel;
import com.yihu.ehr.org.service.Organization;
import com.yihu.ehr.org.service.SecurityClient;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
@RequestMapping("/org")
@Api(protocols = "https", value = "organization", description = "组织机构管理接口", tags = {"机构管理"})
public class OrganizationRestController extends BaseRestController {

    @Autowired
    private OrgManagerService orgManagerService;

    @Autowired
    private SecurityClient securityClient;


    /**
     * 页面初始化
     * @param model
     * @return
     */
    @RequestMapping(value = "/initial", method = RequestMethod.GET)
    public Object orgInitial(Model model) {
        model.addAttribute("contentPage","organization/organization");
        return "pageView";
    }

    /**
     * 弹出框查看
     * @param model
     * @param orgCode
     * @param mode
     * @return
     */
    @RequestMapping(value = "/dialog/orgInfo", method = RequestMethod.GET)
    public Object appInfoTemplate(Model model, String orgCode, String mode) {
        Organization org = orgManagerService.getOrg(orgCode);
        OrgModel orgModel = orgManagerService.getOrgModel(org);
        model.addAttribute("mode",mode);
        model.addAttribute("org",orgModel);
        model.addAttribute("contentPage","organization/organizationInfoDialog");
        return  "simpleView";
    }

    /**
     * 弹出框新增
     * @param model
     * @param mode
     * @return
     */
    @RequestMapping(value = "/dialog/create", method = {RequestMethod.GET})
    public Object createInitial(Model model, String mode) {
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","organization/orgCreateDialog");
        return "generalView";
    }

    /**
     * 机构列表查询
     * @param searchNm
     * @param settledWay
     * @param orgType
     * @param province
     * @param city
     * @param district
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseBody
    public Object searchOrgs(
        @ApiParam(name = "searchNm", value = "搜索条件")
        @RequestParam(value = "searchNm") String searchNm,
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
        @RequestParam(value = "rows") int rows) {

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("orgCode", searchNm);
        conditionMap.put("fullName", searchNm);
        conditionMap.put("settledWay", settledWay);
        conditionMap.put("orgType", orgType);
        conditionMap.put("province", province);
        conditionMap.put("city", city);
        conditionMap.put("district", district);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);


        List<MOrganization> organizationList = orgManagerService.searchOrgDetailModel(conditionMap);
        Integer totalCount = organizationList.size();

        return organizationList;

    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "org", method = RequestMethod.DELETE)
    public Object deleteOrg(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {
        orgManagerService.delete(orgCode);
        return "删除机构成功！";
    }


    /**
     * 激活机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "activity" , method = RequestMethod.PUT)
    public Object activity(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "activityFlag", value = "状态", defaultValue = "")
            @RequestParam(value = "activityFlag") String activityFlag) {
        Organization org = orgManagerService.getOrg(orgCode);
        if("1".equals(activityFlag)){
            org.setActivityFlag(0);
            orgManagerService.update(org);
        }else{
            org.setActivityFlag(1);
            orgManagerService.update(org);
        }
        return "success";
    }


    /**
     * 跟新机构
     * @param orgModel
     * @return
     */
    @RequestMapping(value = "org" , method = RequestMethod.PUT)
    public Object updateOrg(OrgModel orgModel) {
        Map<String, String> message = new HashMap<>();
        Result result = new Result();
        try {
            if (orgModel.getUpdateFlg().equals("0")){
                if(orgManagerService.isExistOrg(orgModel.getOrgCode())){
                    result.setSuccessFlg(false);
                    result.setErrorMsg("该机构已存在!");
                    // // TODO: 2015/12/30 跟新失败，返回指定类型 
                    return result.toJson();
                }
                Organization org = orgManagerService.register(orgModel.getOrgCode(), orgModel.getFullName(), orgModel.getShortName());

                org.setActivityFlag(1);
                //这里做个服务调用更改地址信息
                MAddress location =  new MAddress();
                location.setCity(orgModel.getCity());
                location.setDistrict(orgModel.getDistrict());
                location.setProvince(orgModel.getProvince());
                location.setTown(orgModel.getTown());
                location.setStreet(orgModel.getStreet());
                orgManagerService.saveAddress(location);

                org.setAdmin(orgModel.getAdmin());
                org.setTel(orgModel.getTel());
                org.setOrgType(orgModel.getOrgType());
                org.setSettledWay(orgModel.getSettledWay());
                org.setOrgType(orgModel.getOrgType());
                org.setSettledWay(orgModel.getSettledWay());
                org.addTag(orgModel.getTags());

                orgManagerService.update(org);

                result.setSuccessFlg(true);
                return result.toJson();
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
                //org.getTags().clear();
                org.setTags("");
                org.addTag(orgModel.getTags());
                MAddress location = new MAddress();
                location.setProvince(orgModel.getProvince());
                location.setCity(orgModel.getCity());
                location.setDistrict(orgModel.getDistrict());
                location.setTown(orgModel.getTown());
                orgManagerService.saveAddress(location);
                orgManagerService.update(org);

                result.setSuccessFlg(true);
                result.setErrorMsg("该机构不存在。");
                return result.toJson();
            }

        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("更新失败，请联系管理员");
            return result.toJson();
        }
    }

    /**
     * 根据code获取机构信息
     * @param orgCode
     * @return
     */
    @ApiOperation(value = "根据地址代码获取机构详细信息")
    @RequestMapping(value = "/org", method = RequestMethod.GET)
    public Object getOrg(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {
        OrgModel orgModel = new OrgModel();
        Organization org = orgManagerService.getOrg(orgCode);
        if(org!=null){
            orgModel = orgManagerService.getOrgModel(org);
        }else{
            orgModel.setOrgCode("00000");
        }
        return orgModel;
    }

    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/org/name", method = RequestMethod.GET)
    public Object getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        OrgModel orgModel = new OrgModel();
        List<String> ids = orgManagerService.getIdsByName(name);
        if(ids.size()==0){
            ids.add("00000");
        }
        return ids;
    }



    /**
     * 根据地址获取机构
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/orgs" , method = RequestMethod.GET)
    public Object getOrgs(String province, String city) {

        List<Organization> orgList = orgManagerService.searchByAddress(province,city);
        Map<String, String> orgMap = new HashMap<>();
        for (Organization org : orgList) {
            orgMap.put(org.getOrgCode(), org.getFullName());
        }
        return orgMap;
    }

    @RequestMapping( value = "distributeKey" , method = RequestMethod.POST)
    public Object distributeKey(String orgCode) {
        try {
            String publicKey = securityClient.getOrgPublicKey(orgCode);
            MUserSecurity userSecurity = new MUserSecurity();
            Map<String, String> keyMap = new HashMap<>();
            if (StringUtils.isEmpty(publicKey)) {
                userSecurity = securityClient.createSecurityByOrgCode(orgCode);

            }else{
                //result.setErrorMsg("公钥信息已存在。");
                //这里删除原有的公私钥重新分配
                String userKeyId = securityClient.getUserKeyByOrgCd(orgCode);
                securityClient.deleteSecurity(userSecurity.getId());
                securityClient.deleteUserKey(userKeyId);
                userSecurity = securityClient.createSecurityByOrgCode(orgCode);
            }
            //String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                    + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");

            keyMap.put("publicKey", userSecurity.getPublicKey());
            keyMap.put("validTime", validTime);
            keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
            return keyMap;
        } catch (Exception ex) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("failed");
            return apiErrorEcho;
        }
    }

    @RequestMapping(value = "/validation" ,method = RequestMethod.GET)
    @ResponseBody
    public Object validationOrg(String searchNm){

        Organization org = orgManagerService.getOrg(searchNm);
        if(org == null){
            return "success";
        }else{
            return "fail";
        }

    }

}
