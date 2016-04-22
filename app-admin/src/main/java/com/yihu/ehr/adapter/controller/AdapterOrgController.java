package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseUIController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采集标准
 * Created by zqb on 2015/11/19.
 */
@Controller
@RequestMapping("/adapterorg")
public class AdapterOrgController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("initialOld")
    public String adapterOrgInitOld() {
        return "/adapter/adapterOrg";
    }

    @RequestMapping("initial")
    public String adapterOrgInit(Model model) {
        model.addAttribute("contentPage", "/adapter/adapterOrg/adapterOrg");
        return "pageView";
    }

    /**
     * 第三方标准：新增、修改窗口
     * @param model
     * @param code
     * @param type
     * @param mode
     * @return
     */
    @RequestMapping("template/adapterOrgInfo")
    public Object adapterOrgInfoTemplate(Model model, String code, String type, String mode, String frm) {
        String url = "/adapterOrg/org/"+code;

        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",code);
        params.put("type",type);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                model.addAttribute("rs", "success");
            }
            else {
                model.addAttribute("initType",type);
            }
            model.addAttribute("info", resultStr);
            model.addAttribute("mode",mode);
            model.addAttribute("frm",frm);
            model.addAttribute("contentPage","/adapter/adapterOrg/adapterOrgDialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    //检索第三方标准列表
    @RequestMapping("searchAdapterOrg")
    @ResponseBody
    public Object searchAdapterOrg(String searchNm, int page, int rows, String type) {
        String url = "/adapterOrg/orgs";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        String filters = "";
        if(!StringUtils.isEmpty(searchNm)){
            filters+="name?"+searchNm+" g1;";
        }
        if(!StringUtils.isEmpty(type)){
            filters+= "type="+type+" g2";
        }

        params.put("sorts","");
        params.put("filters",filters);
        params.put("fields","");
        params.put("page",page);
        params.put("size",rows);
        try{
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

    }

    @RequestMapping("getAdapterOrg")
    @ResponseBody
    //获取采集标准
    public Object getAdapterOrg(String code) {

        String url = "/adapterOrg/org/"+code;
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();

        try{
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());

            return envelop;
        }
    }

    //新增采集标准
    @RequestMapping("addAdapterOrg")
    @ResponseBody
    public Object addAdapterOrg(AdapterOrgDetailModel adapterPlanModel) {
        String url="/adapterOrg/org";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try{
            String adapterJsonModel = mapper.writeValueAsString(adapterPlanModel);
            params.put("adapterOrg", adapterJsonModel);

            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);

            envelop = mapper.readValue(resultStr,Envelop.class);
            envelop.setObj(resultStr);

            return envelop;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    //更新采集标准
    @RequestMapping("updateAdapterOrg")
    @ResponseBody
    public Object updateAdapterOrg(String code, String name, String description) {
        String url="/adapterOrg/org/"+code;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",code);
        params.put("name",name);
        params.put("description",description);
        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    //删除采集标准
    @RequestMapping("delAdapterOrg")
    @ResponseBody
    public Object delAdapterOrg(String code) {

        String url = "/adapterOrg/orgs";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("codes",code);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

    }

    @RequestMapping("getAdapterOrgList")
    @ResponseBody
    //获取初始标准列表
    public Object getAdapterOrgList(String type) {
        String url = "/adapterOrg/getAdapterOrgList";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> adapterOrgList = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        //根据类型获取所有采集标准
//        Result result = new Result();
//        List<String> orglist = new ArrayList<>();
//        try {
//            List<XAdapterOrg> adapterOrgList = adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type), orglist);
//            switch (type) {
//                case "1":
//                    //厂商，初始标准只能是厂商
//                    break;
//                case "2":
//                    //医院，初始标准没有限制
//                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1"), orglist));
//                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("3"), orglist));
//                    break;
//                case "3":
//                    //区域,初始标准只能选择厂商或区域
//                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1"), orglist));
//                    break;
//            }
//            List<String> adapterOrgs = new ArrayList<>();
//            if (!adapterOrgList.isEmpty()) {
//                for (XAdapterOrg adapterOrg : adapterOrgList) {
//                    adapterOrgs.add(adapterOrg.getCode() + ',' + adapterOrg.getName());
//                }
//            }
//            result.setObj(adapterOrgs);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    //todo：前端请求方法已被注释，目前没有用到该方法
    @RequestMapping("getOrgList")
    @ResponseBody
    //机构列表
    public Object getOrgList(String type) {
        String url = "/adapterOrg/getAdapterOrgList";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> orgList = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        String searchWay = "";
//        if (type.equals("1")) {
//            //厂商
//            searchWay = "ThirdPartyPlatform";
//        } else if (type.equals("2")) {
//            //医院
//            searchWay = "Hospital";
//        }
//        try {
//            List<XOrganization> organizations = orgManager.search(searchWay);
//            List<String> orgs = new ArrayList<>();
//            if (!organizations.isEmpty()) {
//                for (XOrganization organization : organizations) {
//                    orgs.add(organization.getOrgCode() + ',' + organization.getFullName());
//                }
//            }
//            result.setObj(orgs);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("searchOrgList")
    @ResponseBody
    //查询机构列表
    public Object searchOrgList(String type, String param, int page, int rows) {

        String resultStr = "";
        String filters = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        String orgType = "";

        try {
            String adapterOrgListUrl = "/adapterOrg/orgs";
            Map<String, Object> adapterOrgParams = new HashMap<>();
            adapterOrgParams.put("sorts","");
            adapterOrgParams.put("filters","");
            adapterOrgParams.put("fields","");
            adapterOrgParams.put("page",1);
            adapterOrgParams.put("size",10000);
            String  adapterOrgResultStr = HttpClientUtil.doGet(comUrl + adapterOrgListUrl, adapterOrgParams, username, password);
            Envelop adapterOrgEnvelop = getEnvelop(adapterOrgResultStr);
            List<AdapterOrgModel> adapterOrgModelList = (List<AdapterOrgModel>)getEnvelopList(adapterOrgEnvelop.getDetailModelList(),new ArrayList<AdapterOrgModel>(),AdapterOrgModel.class);

            String orgCodeList = "";
            for(AdapterOrgModel adapterOrgModel : adapterOrgModelList){
                orgCodeList += "," + adapterOrgModel.getCode().toString();
            }

            if(!StringUtils.isEmpty(type)){
                switch (type){
                    case "1":
                        orgType = "ThirdPartyPlatform";
                        break;
                    case "2":
                        orgType = "Hospital";
                        break;
                    default:
                        orgType = "Govement";
                        break;
                }
                filters += "orgType="+orgType;
            }

            if(!StringUtils.isEmpty(param)){
                if(!StringUtils.isEmpty(filters)){
                    filters += ";orgCode?"+param+" g1;fullName?"+param+" g1;pyCode?"+ param +" g1";
                }
                else{
                    filters += "orgCode?"+param+" g1;fullName?"+param+" g1;pyCode?"+ param +" g1";
                }
            }

            if(!StringUtils.isEmpty(orgCodeList)){
                orgCodeList = orgCodeList.substring(1);
                if(!StringUtils.isEmpty(filters)){
                    filters += ";orgCode<>"+ orgCodeList;
                }
                else{
                    filters += "orgCode<>"+ orgCodeList;
                }
            }

            params.put("fields","");
            params.put("filters",filters);
            params.put("sorts","");
            params.put("address","");
            params.put("size",rows);
            params.put("page",page);
            String url = "/organizations";

            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("searchAdapterOrgList")
    @ResponseBody
    public Object searchAdapterOrgList(String type, String param, int page, int rows) {
        String url = "/adapterOrg/orgs";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        if("2".equals(type))
            type = "1,2,3";
        else if("3".equals(type))
            type = "1,3";

        String filters = "";
        if(!StringUtils.isEmpty(param)){
            filters+="name?"+param+" g1;";
        }
        if(!StringUtils.isEmpty(type)){
            filters+= "type="+type+" g2";
        }

        params.put("sorts","");
        params.put("filters",filters);
        params.put("fields","");
        params.put("page",page);
        params.put("size",rows);

        try {

            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }
//        Result result = new Result();
//        Map<String, Object> conditionMap = new HashMap<>();
//        param = param == null ? "" : param;
//        conditionMap.put("key", param);
//        conditionMap.put("page", page);
//        conditionMap.put("pageSize", rows);
//        try {
//            List typeLs = new ArrayList<>();
//            conditionMap.put("typeLs", conventionalDictEntry.getAdapterType(type));
//            switch (type) {
//                case "1":
//                    //厂商，初始标准只能是厂商
//                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                    break;
//                case "2":
//                    //医院，初始标准没有限制
//                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                    typeLs.add(conventionalDictEntry.getAdapterType("2"));
//                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
//                    break;
//                case "3":
//                    //区域,初始标准只能选择厂商或区域
//                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
//                    break;
//            }
//            conditionMap.put("typeLs", typeLs);
//            List<XAdapterOrg> adapterOrgList = adapterOrgManager.searchAdapterOrg(conditionMap);
//            int total = adapterOrgManager.searchAdapterOrgInt(conditionMap);
//            result = getResult(adapterOrgList, total, page, rows);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
//    }
}
