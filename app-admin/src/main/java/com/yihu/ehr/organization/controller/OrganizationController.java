package com.yihu.ehr.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

        String getOrgUrl = "/organizations/"+orgCode;
        String resultStr = "";
        try{
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, username, password);
        } catch (Exception e){
            LogService.getLogger(OrganizationController.class).error(e.getMessage());
        }
        model.addAttribute("mode",mode);
        model.addAttribute("envelop",resultStr);
        model.addAttribute("contentPage","organization/organizationInfoDialog");
        return  "simpleView";
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
        //TODO 能访问，地址检索问题、多条件检索问题
        String url = "/organizations";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        StringBuffer filters = new StringBuffer();
        if(!StringUtils.isEmpty(searchNm)){
            filters.append("orgCode?"+searchNm+";");
        }
        if(!StringUtils.isEmpty(searchWay)){
            filters.append("settledWay="+searchWay+";");
        }
        if(!StringUtils.isEmpty(orgType)){
            filters.append("orgType="+orgType+";");
        }
        //有修改内容：js文件（属性名、检索值的取得）
        //TODO 根据地址的过滤
        //微服务单表查询，没办法
        //api网关查出org，查出地址，在筛选-----
        params.put("fields","");
        params.put("filters",filters);
        params.put("sorts","");
        params.put("size",rows);
        params.put("page",page);

//        String url = "/organization/organizations";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("orgCode", searchNm);
//        params.put("fullName", searchNm);
//        params.put("searchWay", searchWay);
//        params.put("orgType", orgType);
//        params.put("province", province);
//        params.put("city", city);
//        params.put("district", district);
//        params.put("page", page);
//        params.put("pageSize", rows);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("deleteOrg")
    @ResponseBody
    public Object deleteOrg(String orgCode) {
        String getOrgUrl = "/organizations/"+orgCode;
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + getOrgUrl, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;

    }

    /**
     *
     * @param orgCode
     * @return
     */
    @RequestMapping("activity")
    @ResponseBody
    public Object activity(String orgCode,String activityFlag) {

        String url = "/organizations/"+orgCode+"/"+activityFlag;
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                envelop.setSuccessFlg(true);
            }
            else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return envelop;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @RequestMapping("updateOrg")
    @ResponseBody
    public Object updateOrg(String orgModel,String addressModel,String mode) {
        //新增或修改 根据mode 选择
        String url = "/organizations";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("mOrganizationJsonData",orgModel);
        params.put("geography_model_json_data",addressModel);
        try {
            if("new".equals(mode)){
                resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            }else {
                ObjectMapper objectMapper = new ObjectMapper();
                //读取参数，转化为模型
                OrgDetailModel org = objectMapper.readValue(orgModel,OrgDetailModel.class);
                //查询数据库得到对应的模型
                String getOrgUrl = "/organizations/"+org.getOrgCode();
                resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, params, username, password);
                Map<String,Object> map = objectMapper.readValue(resultStr,Map.class);
                Object object = map.get("obj");
                String strOrg = objectMapper.writeValueAsString(object);
                OrgDetailModel orgForUpdate =(objectMapper.readValue(strOrg,OrgDetailModel.class));
                //将修改值存储到查询返回的对象中
                orgForUpdate.setFullName(org.getFullName());
                orgForUpdate.setShortName(org.getShortName());
                orgForUpdate.setSettledWay(org.getSettledWay());
                orgForUpdate.setAdmin(org.getAdmin());
                orgForUpdate.setTel(org.getTel());
                orgForUpdate.setOrgType(org.getOrgType());
                orgForUpdate.setTags(org.getTags());
                String mOrgUpdateJson = objectMapper.writeValueAsString(orgForUpdate);
                params.put("mOrganizationJsonData",mOrgUpdateJson);
                resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            }
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("getOrg")
    @ResponseBody
    public Object getOrg(String orgCode) {

        String getOrgUrl = "/organizations/"+orgCode;
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",orgCode);

        Envelop envelop = new Envelop();
        String resultStr = "";
        try{
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("distributeKey")
    @ResponseBody
    public Object distributeKey(String orgCode) {

        String getOrgUrl = "/organizations/key";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("org_code",orgCode);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + getOrgUrl, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }


    @RequestMapping("validationOrg")
    @ResponseBody
    public Object validationOrg(String orgCode){
        //通过
        String getOrgUrl = "/organizations/existence/"+orgCode;
        String resultStr = "";
        Envelop envelop = new Envelop();
        try {
            resultStr = HttpClientUtil.doGet(comUrl + getOrgUrl, username, password);
            if(!Boolean.parseBoolean(resultStr)){
                envelop.setSuccessFlg(true);
            }
            else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return envelop;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }
}
