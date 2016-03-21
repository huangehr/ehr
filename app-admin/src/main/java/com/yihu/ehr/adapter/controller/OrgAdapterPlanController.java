package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.adapter.service.OrgAdapterPlanService;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2015/10/27.
 */
@Controller
@RequestMapping("/adapter")
public class OrgAdapterPlanController extends ExtendController<OrgAdapterPlanService> {

    public OrgAdapterPlanController() {
        this.init(
                "/adapter/adapter",                 //列表页面url
                "/adapter/adapterInfoDialog"      //编辑页面url
        );

        Map<String, String> comboKv = new HashMap<>();
        comboKv.put("code", "id");
        comboKv.put("value", "name");
        comboKv.put("org", "org");
        this.comboKv = comboKv;
    }

    /**
     * 跳转至定制页面
     *
     * @param model
     * @param planId
     * @param version
     * @return
     */
    @RequestMapping("/getAdapterCustomize")
    public Object getAdapterCustomize(Model model, Long planId, String version) {

        try {
            String url = "/adapter/plan/adapterCustomizes/" + planId;
            String resultStr = "";
            Map<String, Object> params = new HashMap<>();
            params.put("version", version);
            resultStr = HttpClientUtil.doGet(service.comUrl + url, params, service.username, service.password);
            model.addAttribute("planId", planId);
            model.addAttribute("allData", resultStr);
            model.addAttribute("contentPage", "adapter/adapterCustomize");
            return "generalView";
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 定制数据集
     * 2015-12-31  定制速度优化以及添加事务控制 by lincl
     *
     * @param planId
     * @param customizeData
     * @return
     */
    @RequestMapping("/adapterDataSet")
    @ResponseBody
    public Object adapterDataSet(Long planId, String customizeData) {

        try {
            customizeData = customizeData.replace("DataSet", "").replace("MetaData", "");
            String url = "/adapter/plan/adapterDataSet/" + planId;
            String resultStr = "";
            Envelop result = new Envelop();
            Map<String, Object> params = new HashMap<>();
            params.put("customizeData", customizeData);

            resultStr = HttpClientUtil.doPost(service.comUrl + url, params, service.username, service.password);
//            RestTemplate template = new RestTemplate();
//            Map<String,Object> conditionMap = new HashMap<>();
//            conditionMap.put("customizeData", toJson(customizeData));
//            resultStr = template.postForObject(service.comUrl+url,conditionMap,String.class);
//            RestTemplates template = new RestTemplates();
//            MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<String, String>();
//            conditionMap.add("customizeData", customizeData);
//            resultStr = template.doPost(service.comUrl+url, conditionMap);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg("定制失败！");
            }
            return result;
        } catch (Exception e) {
            return systemError();
        }
    }

//    @RequestMapping("getOrgList")
//    @ResponseBody
//    public Object getOrgList(String type,String version,String mode) {
//        String url = "/plan/getAdapterPlanList";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("mode",mode);
//        params.put("type",type);
//        params.put("versionCode",version);
//        try {
//            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
////            ObjectMapper mapper = new ObjectMapper();
////            List<Map> adapterPlan = Arrays.asList(mapper.readValue(resultStr, Map[].class));
//            result.setSuccessFlg(true);
//            result.setObj(resultStr);
//            return result;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        //根据类型获取所有采集标准
////        Result result = new Result();
////        try {
////            List<XAdapterOrg> adapterOrgList=null;
////            List<XOrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.getOrgAdapterPlan("",version);
////            List<String> orgList = new ArrayList<>();
////            if(!mode.equals("modify")){
////                for(XOrgAdapterPlan xOrgAdapterPlan:orgAdapterPlans){
////                    orgList.add(xOrgAdapterPlan.getOrg());
////                }
////            }
////            adapterOrgList=adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type),orgList);
////
////            List<Map> adapterOrgs = new ArrayList<>();
////
////            if (!adapterOrgList.isEmpty()){
////                for (XAdapterOrg adapterOrg : adapterOrgList) {
////                    Map<String,String> map = new HashMap<>();
////                    map.put("code",adapterOrg.getCode());
////                    map.put("value",adapterOrg.getName());
////                    adapterOrgs.add(map);
////                }
////            }
////            result.setSuccessFlg(true);
////            result.setDetailModelList(adapterOrgs);
////        } catch (Exception ex){
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//    }


//    @RequestMapping("orgIsExistData")
//    @ResponseBody
//    public Object orgIsExistData(String org){
//    }


//    // 获取所有第三方标准的采集机构
//    @RequestMapping("getAdapterOrgList")
//    @ResponseBody
//    public Object getAdapterOrgList(String type){
//        String url = "/plan/getAdapterOrgList";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("type",type);
//        try {
//            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
////            ObjectMapper mapper = new ObjectMapper();
////            List<Map> adapterPlan = Arrays.asList(mapper.readValue(resultStr, Map[].class));
//            result.setSuccessFlg(true);
//            result.setObj(resultStr);
//            return result;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        Result result = new Result();
////        AdapterType adapterType = conventionalDictEntry.getAdapterType(type);
////        List<String> orgList = new ArrayList<>();
////        List<Map> adapterOrgs = new ArrayList<>();
////        try{
////            List<XAdapterOrg> adapterOrgList = adapterOrgManager.searchAdapterOrg(adapterType, orgList);
////            if (!adapterOrgList.isEmpty()) {
////                for (XAdapterOrg adapterOrg : adapterOrgList) {
////                    Map<String, String> map = new HashMap<>();
////                    map.put("code", adapterOrg.getCode());
////                    map.put("value", adapterOrg.getName());
////                    adapterOrgs.add(map);
////                }
////            }
////            result.setSuccessFlg(true);
////            result.setDetailModelList(adapterOrgs);
////        } catch (Exception ex){
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//    }


}
