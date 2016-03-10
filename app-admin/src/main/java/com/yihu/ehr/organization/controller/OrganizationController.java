package com.yihu.ehr.organization.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@Controller
@RequestMapping("/organization")
public class OrganizationController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;
    @RequestMapping("initial")
    public String orgInitial(Model model) {
        model.addAttribute("contentPage","organization/organization");
        return "pageView";
    }

    @RequestMapping("dialog/orgInfo")
    public String orgInfoTemplate(Model model, String orgCode, String mode) {

        String getOrgUrl = "/organization/org_model";
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);

        String resultStr = "";
        try{
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, params, username, password);

            model.addAttribute("mode",mode);
            model.addAttribute("org",resultStr);
            model.addAttribute("contentPage","organization/organizationInfoDialog");
            return  "simpleView";
        }
        catch (Exception e){

            model.addAttribute("mode",mode);
            model.addAttribute("org",resultStr);
            model.addAttribute("contentPage","organization/organizationInfoDialog");
            return  "simpleView";
        }

        /*
        XOrganization org = orgManager.getOrg(orgCode);
        OrgModel orgModel = orgManager.getOrgModel(org);

        model.addAttribute("mode",mode);
        model.addAttribute("org",orgModel);
        model.addAttribute("contentPage","organization/organizationInfoDialog");
        return  "simpleView";
        */
    }

    @RequestMapping("dialog/create")
    public String createInitial(Model model,String mode) {
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","organization/orgCreateDialog");
        return "generalView";
    }

    @RequestMapping(value = "searchOrgs",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object searchOrgs(String searchNm, String searchWay,String orgType, String province, String city, String district, int page, int rows) {

        String url = "/organization/organizations";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode", searchNm);
        params.put("fullName", searchNm);
        params.put("searchWay", searchWay);
        params.put("orgType", orgType);
        params.put("province", province);
        params.put("city", city);
        params.put("district", district);
        params.put("page", page);
        params.put("pageSize", rows);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo:jsp展示需调整
            //todo:后台直接返回总行数及相应的数据集
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

        /*
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("orgCode", searchNm);
        conditionMap.put("fullName", searchNm);

        conditionMap.put("searchWay", searchWay);
        conditionMap.put("orgType", orgType);
        conditionMap.put("province", province);
        conditionMap.put("city", city);
        conditionMap.put("district", district);

        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);

        List<OrgDetailModel> detailModelList = orgManager.searchOrgDetailModel(conditionMap);
        Integer totalCount = orgManager.searchInt(conditionMap);
        Result result = getResult(detailModelList, totalCount, page, rows);

        return result.toJson();*/
    }

    @RequestMapping("deleteOrg")
    @ResponseBody
    public Object deleteOrg(String orgCode) {

        String getOrgUrl = "/organization/organization";
        String resultStr = "";
        Envelop result = new Envelop();

        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);

        try {
            resultStr = HttpClientUtil.doDelete(comUrl + getOrgUrl, params, username, password);

            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result;

        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
        /*
        try {
            orgManager.delete(orgCode);
            Result result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {
            Result result = getSuccessResult(false);
            return result.toJson();
        }*/
    }

    /**
     *
     * @param orgCode
     * @return
     */
    @RequestMapping("activity")
    @ResponseBody
    public Object activity(String orgCode,String activityFlag) {

        String url = "/organization/activity";
        String resultStr = "";
        Envelop result = new Envelop();

        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);

        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result;
        }

        /*
        try {
            XOrganization org = orgManager.getOrg(orgCode);
            if("1".equals(activityFlag)){
                org.setActivityFlag(0);
                orgManager.update(org);
            }else{
                org.setActivityFlag(1);
                orgManager.update(org);
            }

            Result result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {
            Result result = getSuccessResult(false);
            return result.toJson();
        }*/
    }

    @RequestMapping("updateOrg")
    @ResponseBody
    public Object updateOrg(String orgModel) {

        String url = "/organization/org";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("orgModel",orgModel);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            //todo: 需要在后台追加唯一性判断
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

        /*
        Map<String, String> message = new HashMap<>();
        Result result = new Result();
        try {
            if (orgModel.getUpdateFlg().equals("0")){
                if(orgManager.isExistOrg(orgModel.getOrgCode())){
                    result.setSuccessFlg(false);
                    result.setErrorMsg("该机构已存在!");
                    return result.toJson();
                }
                XOrganization org = orgManager.register(orgModel.getOrgCode(), orgModel.getFullName(), orgModel.getShortName());

                org.setActivityFlag(1);
                org.getLocation().setCity(orgModel.getCity());
                org.getLocation().setDistrict(orgModel.getDistrict());
                org.getLocation().setProvince(orgModel.getProvince());
                org.getLocation().setTown(orgModel.getTown());
                org.getLocation().setStreet(orgModel.getStreet());

                org.setAdmin(orgModel.getAdmin());
                org.setTel(orgModel.getTel());
                org.setType(absDictEManage.getOrgType(orgModel.getOrgType()));
                org.setSettleWay(absDictEManage.getSettledWay(orgModel.getSettledWay()));
                org.addTag(orgModel.getTags());

                orgManager.update(org);

                result.setSuccessFlg(true);
                return result.toJson();
            }
            else{
                result.setSuccessFlg(orgManager.update(orgModel));
                result.setErrorMsg("该机构不存在。");
                return result.toJson();
            }

        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("更新失败，请联系管理员");
            return result.toJson();
        }*/
    }

    @RequestMapping("getOrg")
    @ResponseBody
    public Object getOrg(String orgCode) {

        String getOrgUrl = "/organization/org_model";
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);

        Envelop result = new Envelop();
        String resultStr = "";

        try{
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, params, username, password);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

        /*
        XOrganization org = orgManager.getOrg(orgCode);
        OrgModel orgModel = orgManager.getOrgModel(org);
        Map<String, OrgModel> data = new HashMap<>();
        data.put("orgModel", orgModel);

        Result result = new Result();
        result.setObj(data);
        return result.toJson();*/
    }

    @RequestMapping("distributeKey")
    @ResponseBody
    public Object distributeKey(String orgCode) {

        String getOrgUrl = "/organization/distributeKey";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

        /*
        try {
            Result result = getSuccessResult(true);
            XUserSecurity userSecurity = securityManager.getUserPublicKeyByOrgCd(orgCode);
            Map<String, String> keyMap = new HashMap<>();
            if (userSecurity == null) {
                userSecurity = securityManager.createSecurityByOrgCd(orgCode);

            }else{
                //result.setErrorMsg("公钥信息已存在。");
                //这里删除原有的公私钥重新分配
                String userKeyId = securityManager.getUserKeyByOrgCd(orgCode);
                securityManager.deleteSecurity(userSecurity.getId());
                securityManager.deleteUserKey(userKeyId);
                userSecurity = securityManager.createSecurityByOrgCd(orgCode);
            }
            //String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);

            keyMap.put("publicKey", userSecurity.getPublicKey());
            keyMap.put("validTime", validTime);
            keyMap.put("startTime", DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
            result.setObj(keyMap);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception ex) {
            Result result = getSuccessResult(false);
            return result.toJson();
        }*/
    }


    @RequestMapping("validationOrg")
    @ResponseBody
    public Object validationOrg(String orgCode){

        String getOrgUrl = "/organization/isOrgCodeExist";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

        /*
        XOrganization org = orgManager.getOrg(searchNm);
        if(org == null){
            Result result = getSuccessResult(true);
            return result.toJson();
        }
        Result result = getSuccessResult(false);
        return  result.toJson();*/
    }
}
