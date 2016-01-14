package com.yihu.ehr.org.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.model.AddressModel;
import com.yihu.ehr.model.OrganizationModel;
import com.yihu.ehr.org.service.OrgManagerService;
import com.yihu.ehr.org.service.OrgModel;
import com.yihu.ehr.org.service.Organization;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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
//@RequestMapping(ApiVersionPrefix.CommonVersion + "/organization")
@Api(protocols = "https", value = "organization", description = "组织机构管理接口", tags = {"机构管理"})
public class OrganizationRestController extends BaseRestController {

    @Autowired
    private OrgManagerService orgManagerService;

//    @Autowired
//    private SecurityManagerService securityManagerService;


    /**
     * 根据id获取地址客户端测试
     * @param id
     * @return
     */
    @RequestMapping(value = "/address", method = RequestMethod.GET)
    public Object address(String id) {
        Object address = orgManagerService.getAddressById(id);
        return address;
    }


    /**
     * 根据id获取地址客户端测试
     * @param id
     * @return
     */
    @RequestMapping(value = "/address/add", method = RequestMethod.PUT)
    public Object saveAddress(String id) {
        AddressModel addressModel = new AddressModel();
        addressModel.setId("sasaas");
        addressModel.setCountry("dada");
        addressModel.setProvince("sasa");
        addressModel.setCity("sasa");
        //addressModel.setDistrict("wqwqwq");
        addressModel.setTown("sasa");
        addressModel.setStreet("sasa");
        addressModel.setExtra("sasa");
        addressModel.setPostalCode("sasa");
        String addressId = orgManagerService.saveAddress(addressModel);
        return addressId;
    }






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
    @RequestMapping(value = "searchOrgs", method = RequestMethod.GET)
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

        BaseController baseController = new BaseController();

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


        List<OrganizationModel> organizationModel = orgManagerService.searchOrgDetailModel(conditionMap);
        Integer totalCount = organizationModel.size();

        Result result = baseController.getResult(organizationModel, totalCount, page, rows);

        return result;

    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public Object deleteOrg(String orgCode) {
        orgManagerService.delete(orgCode);
        return "删除机构成功！";
    }


    /**
     * 激活机构
     * @param orgCode
     * @return
     */
    @RequestMapping("activity")
    @ResponseBody
    public Object activity(String orgCode,String activityFlag) {
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
    @RequestMapping("updateOrg")
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
                AddressModel location =  new AddressModel();
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
                AddressModel location = new AddressModel();
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
    @RequestMapping(value = "/getOrg", method = RequestMethod.GET)
    public Object getOrg(String orgCode) {
        Map<String, OrgModel> result = new HashMap<>();

        Organization org = orgManagerService.getOrg(orgCode);
        OrgModel orgModel = orgManagerService.getOrgModel(org);

        result.put("orgModel", orgModel);
        return result;
    }

    /**
     * 根据地址获取机构
     * @param province
     * @param city
     * @return
     */
    @RequestMapping("getOrgs")
    @ResponseBody
    public Object getOrgs(String province, String city) {

        List<Organization> orgList = orgManagerService.searchByAddress(province,city);
        Map<String, String> orgMap = new HashMap<>();
        for (Organization org : orgList) {
            orgMap.put(org.getOrgCode(), org.getFullName());
        }
        return orgMap;
    }

//    @RequestMapping("distributeKey")
//    @ResponseBody
//    public String distributeKey(String orgCode) {
//        try {
//            Result result = getSuccessResult(true);
//            XUserSecurity userSecurity = securityManager.getUserPublicKeyByOrgCd(orgCode);
//            Map<String, String> keyMap = new HashMap<>();
//            if (userSecurity == null) {
//                userSecurity = securityManager.createSecurityByOrgCd(orgCode);
//
//            }else{
//                //result.setErrorMsg("公钥信息已存在。");
//                //这里删除原有的公私钥重新分配
//                String userKeyId = securityManager.getUserKeyByOrgCd(orgCode);
//                securityManager.deleteSecurity(userSecurity.getId());
//                securityManager.deleteUserKey(userKeyId);
//                userSecurity = securityManager.createSecurityByOrgCd(orgCode);
//            }
//            //String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
//            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
//                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
//
//            keyMap.put("publicKey", userSecurity.getPublicKey());
//            keyMap.put("validTime", validTime);
//            keyMap.put("startTime", DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
//            result.setObj(keyMap);
//            result.setSuccessFlg(true);
//            return result.toJson();
//        } catch (Exception ex) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
//    }
    @RequestMapping("validationOrg")
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
