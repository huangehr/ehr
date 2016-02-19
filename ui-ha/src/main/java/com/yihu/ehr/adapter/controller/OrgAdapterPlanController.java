package com.yihu.ehr.adapter.controller;

import com.yihu.ha.adapter.model.*;
import com.yihu.ha.constrant.ErrorCode;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.dict.model.common.XConventionalDictEntry;
import com.yihu.ha.std.model.*;
import com.yihu.ha.util.HttpClientUtil;
import com.yihu.ha.util.ResourceProperties;
import com.yihu.ha.util.controller.BaseController;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * Created by zqb on 2015/10/27.
 */
@Controller
@RequestMapping("/adapter")
public class OrgAdapterPlanController extends BaseController {
    @Resource(name = Services.OrgAdapterPlanManager)
    private XOrgAdapterPlanManager orgAdapterPlanManager;

    @Resource(name = Services.CDAVersionManager)
    private XCDAVersionManager cdaVersionManager;

    @Resource(name= Services.DataSetManager)
    private XDataSetManager dataSetManager;

    @Resource(name=Services.MetaDataManager)
    private XMetaDataManager metaDataManager;

    @Resource(name = Services.ConventionalDictEntry)
    private XConventionalDictEntry conventionalDictEntry;
    @Resource(name = Services.AdapterOrgManager)
    private XAdapterOrgManager adapterOrgManager;

    public OrgAdapterPlanController(){}

    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("initial")
    public String orgAdapterInit(Model model) {
        model.addAttribute("contentPage","/adapter/adapter");
        return "pageView";
    }

    @RequestMapping("searchAdapterPlan")
    @ResponseBody
    //适配方案搜索
    public String searchAdapterPlan(String searchNm,String searchType,String searchOrg, int page, int rows){
        String url = "/plan/adapterPlans";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("code",searchNm);
        params.put("name",searchNm);
        params.put("type",searchType);
        params.put("orgCode",searchOrg);
        params.put("page",page);
        params.put("rows",rows);
        try{
            //todo 后台转换成result后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("code", searchNm);
//            conditionMap.put("name", searchNm);
//            conditionMap.put("org", searchOrg);
//            conditionMap.put("page", page);
//            conditionMap.put("pageSize", rows);
//            List<AdapterType> typeLs = new ArrayList<>();
//            if (StringUtil.isEmpty(searchType)){
//                typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                typeLs.add(conventionalDictEntry.getAdapterType("2"));
//                typeLs.add(conventionalDictEntry.getAdapterType("3"));
//            }else{
//                typeLs.add(conventionalDictEntry.getAdapterType(searchType));
//            }
//            conditionMap.put("typeLs", typeLs);
//
//            List<AdapterPlanBrowserModel> orgAdapterPlans = orgAdapterPlanManager.searchAdapterPlanBrowser(conditionMap);
//            Integer totalCount = orgAdapterPlanManager.searchOrgAdapterPlanInt(conditionMap);
//            result = getResult(orgAdapterPlans, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("getAdapterPlan")
    //获取适配方案信息
    public String getAdapterPlan(Model model,Long id,String mode){
        String url = "/plan/adapterPlan";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if (id!=null){
                //修改
                //todo 后台转换成model后传前台
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            }
            model.addAttribute("adapterPlan", resultStr);
            model.addAttribute("mode",mode);
            model.addAttribute("contentPage","/adapter/adapterInfoDialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        AdapterPlanModel planModel=null;
//        if (id!=null){
//            //修改
//            XOrgAdapterPlan orgAdapterPlan  = orgAdapterPlanManager.getOrgAdapterPlan(id);
//            planModel = orgAdapterPlanManager.getOrgAdapterPlan(orgAdapterPlan);
//            planModel.setCode(StringUtil.latinString(planModel.getCode()));
//            planModel.setName(StringUtil.latinString(planModel.getName()));
//            planModel.setDescription(StringUtil.latinString(planModel.getDescription()));
//        }
//        model.addAttribute("adapterPlan",planModel);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","adapter/adapterInfoDialog");
//        return "simpleView";
    }

    //todo 前端没有找到该路径的请求方法
    @RequestMapping("getAdapterPlanById")
    @ResponseBody
    public String getAdapterPlanById(Long id) {
        String url = "/plan/adapterPlan";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            AdapterPlanModel planModel = mapper.readValue(resultStr, AdapterPlanModel.class);
            result.setObj(planModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try{
//            XOrgAdapterPlan orgAdapterPlan  = orgAdapterPlanManager.getOrgAdapterPlan(id);
//            AdapterPlanModel planModel = orgAdapterPlanManager.getOrgAdapterPlan(orgAdapterPlan);
//            result.setObj(planModel);
//            result.setSuccessFlg(true);
//        }catch (Exception es){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    private XOrgAdapterPlan getOrgAdapterPlan(Long id) {
        return id==null? new OrgAdapterPlan(): orgAdapterPlanManager.getOrgAdapterPlan(id);
    }

    /**
     * 更新适配方案
     * 2015-12-31  新增速度优化以及添加事务控制 by lincl
     * @param adapterPlanModel
     * @return
     */
    @RequestMapping("updateAdapterPlan")
    @ResponseBody
    public String updateAdapterPlan(AdapterPlanModel adapterPlanModel, String isCover){
        String url = "";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        //todo：网�? orgIsExistData 没有id属�??
        params.put("id",adapterPlanModel.getId());
        params.put("orgCode",adapterPlanModel.getCode());
        try {
            //todo: 后台先根据id取code判断是否与当前code�?致，不一致的情况再判断当前code在数据库是否已经存在
            url="/plan/orgIsExistData";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("codeNotUnique�?");
                return result.toJson();
            }
            //更新或新�?
            params = new HashMap<>();
/*            if (adapterPlanModel.getId()==null) {
                url = "/plan/addAdapterPlan";
                params.put("isCover",isCover);
            }else {
                url = "/plan/updateAdapterPlan";
            }*/
            url = "/plan/updateAdapterPlan";

            params.put("adapterPlanModel",adapterPlanModel);
            params.put("isCover",isCover);
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            AdapterPlanModel planModel = mapper.readValue(resultStr, AdapterPlanModel.class);
            result.setObj(planModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }

//        try {
//            Result result = new Result();
//            XOrgAdapterPlan orgAdapterPlan = getOrgAdapterPlan(adapterPlanModel.getId());
//            boolean checkCode = true;
//            if(adapterPlanModel.getId()!=null && adapterPlanModel.getCode().equals(orgAdapterPlan.getCode()))
//                checkCode=false;
//            if(checkCode && orgAdapterPlanManager.isAdapterCodeExist(adapterPlanModel.getCode())){
//                result.setErrorMsg("codeNotUnique");
//                return result.toJson();
//            }
//            orgAdapterPlan.setCode(adapterPlanModel.getCode());
//            orgAdapterPlan.setName(adapterPlanModel.getName());
//            orgAdapterPlan.setDescription(adapterPlanModel.getDescription());
//            orgAdapterPlan.setVersion(cdaVersionManager.getVersion(adapterPlanModel.getVersion()));
//            orgAdapterPlan.setType(conventionalDictEntry.getAdapterType(adapterPlanModel.getType()));
//            orgAdapterPlan.setOrg(adapterPlanModel.getOrg());
//            orgAdapterPlan.setParentId(adapterPlanModel.getParentId());
//            orgAdapterPlan.setOrg(adapterPlanModel.getOrg());
//            orgAdapterPlan.setParentId(adapterPlanModel.getParentId());
//            if (adapterPlanModel.getId()==null){
//                orgAdapterPlan.setStatus(0);
//                orgAdapterPlanManager.addOrgAdapterPlan(orgAdapterPlan, isCover);
//            }else{
//                orgAdapterPlanManager.updateOrgAdapterPlan(orgAdapterPlan);
//            }
//
//            result = getSuccessResult(true);
//            return result.toJson();
//        }catch (Exception e){
//            e.printStackTrace();
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("orgIsExistData")
    @ResponseBody
    public String orgIsExistData(String org){
        String url = "/plan/orgIsExistData";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode",org);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            result.setSuccessFlg(Boolean.parseBoolean(resultStr));
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            boolean rtn = adapterOrgManager.isExistData(org);
//            result.setSuccessFlg(rtn);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("delAdapterPlan")
    @ResponseBody
    //删除适配方案
    public String delAdapterPlan(@RequestParam("id[]") Long[] id){
        String url = "/plan/delAdapterPlan";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("ids",id);
        try {
            // Todo 网关的url请求方式为get ？？
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        int rtn = orgAdapterPlanManager.deleteOrgAdapterPlan(id);
//        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
//        return result.toJson();
    }

    @RequestMapping("getAdapterPlanList")
    @ResponseBody
    //获取适配方案列表
    public String getAdapterPlanList(String type, String version){
        String url = "/plan/getAdapterPlanList";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        params.put("versionCode",version);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            List<Map> adapterPlan = Arrays.asList(mapper.readValue(resultStr, Map[].class));
            result.setSuccessFlg(true);
            result.setDetailModelList(adapterPlan);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            //根据类型和版本获取所有方�?
//            List<XOrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.getOrgAdapterPlan(type,version);
//            List<Map> adapterPlan = new ArrayList<>();
//            if (!orgAdapterPlans.isEmpty()){
//                for (XOrgAdapterPlan plan : orgAdapterPlans) {
//                    Map<String,String> map = new HashMap<>();
//                    map.put("code",plan.getId().toString());
//                    map.put("value",plan.getName());
//                    map.put("org", plan.getOrg());
//                    adapterPlan.add(map);
//                }
//            }
//            result.setDetailModelList(adapterPlan);
//            result.setSuccessFlg(true);
//        } catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("getOrgList")
    @ResponseBody
    //获取适配方案列表
    public String getOrgList(Model model,String type,String version,String mode) {
        String url = "/plan/getAdapterPlanList";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("mode",mode);
        params.put("type",type);
        params.put("versionCode",version);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            List<Map> adapterPlan = Arrays.asList(mapper.readValue(resultStr, Map[].class));
            result.setSuccessFlg(true);
            result.setDetailModelList(adapterPlan);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        //根据类型获取�?有采集标�?
//        Result result = new Result();
//        try {
//            List<XAdapterOrg> adapterOrgList=null;
//            List<XOrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.getOrgAdapterPlan("",version);
//            List<String> orgList = new ArrayList<>();
//            if(!mode.equals("modify")){
//                for(XOrgAdapterPlan xOrgAdapterPlan:orgAdapterPlans){
//                    orgList.add(xOrgAdapterPlan.getOrg());
//                }
//            }
//            adapterOrgList=adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type),orgList);
//
//            List<Map> adapterOrgs = new ArrayList<>();
//
//            if (!adapterOrgList.isEmpty()){
//                for (XAdapterOrg adapterOrg : adapterOrgList) {
//                    Map<String,String> map = new HashMap<>();
//                    map.put("code",adapterOrg.getCode());
//                    map.put("value",adapterOrg.getName());
//                    adapterOrgs.add(map);
//                }
//            }
//            result.setSuccessFlg(true);
//            result.setDetailModelList(adapterOrgs);
//        } catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    // 获取�?有第三方标准的采集机�?
    @RequestMapping("getAdapterOrgList")
    @ResponseBody
    public String getAdapterOrgList(String type){
        String url = "/plan/getAdapterOrgList";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            List<Map> adapterPlan = Arrays.asList(mapper.readValue(resultStr, Map[].class));
            result.setSuccessFlg(true);
            result.setDetailModelList(adapterPlan);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        AdapterType adapterType = conventionalDictEntry.getAdapterType(type);
//        List<String> orgList = new ArrayList<>();
//        List<Map> adapterOrgs = new ArrayList<>();
//        try{
//            List<XAdapterOrg> adapterOrgList = adapterOrgManager.searchAdapterOrg(adapterType, orgList);
//            if (!adapterOrgList.isEmpty()) {
//                for (XAdapterOrg adapterOrg : adapterOrgList) {
//                    Map<String, String> map = new HashMap<>();
//                    map.put("code", adapterOrg.getCode());
//                    map.put("value", adapterOrg.getName());
//                    adapterOrgs.add(map);
//                }
//            }
//            result.setSuccessFlg(true);
//            result.setDetailModelList(adapterOrgs);
//        } catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }
    @RequestMapping("getAdapterCustomize")
    public String getAdapterCustomize(Model model,Long planId,String version) throws IOException {
        String url = "/plan/getAdapterCustomize";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("planId",planId); //todo：网关缺少planId属�??
        params.put("versionCode",version);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            model.addAttribute("planId",planId);
            model.addAttribute("allData",resultStr);//todo：将标准数据集与定制数据集以对象数组形式�?起传前台，前台接收解�?
            model.addAttribute("contentPage","adapter/adapterCustomize");
            return "generalView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        String dataSetName,metaDataName;
//        String id;
//        boolean check=true;     //是否勾�??
//        boolean std=false;      //标准是否添加根节�?
//        boolean adapter=false;  //定制是否添加根节�?
//        long childCheckCount;
//        //获取�?有定制数据集
//        List<Long> adapterDataSetList = orgAdapterPlanManager.getAdapterDataSet(planId);
//        List<XAdapterDataSet> adapterMetaDataList = orgAdapterPlanManager.getAdapterMetaData(planId);
//        XCDAVersion innerVersion = cdaVersionManager.getVersion(version);
//        List<AdapterCustomize> adapterCustomizeList = new ArrayList<>();
//        //数据�?
//        for(Long adapterDataSet:adapterDataSetList){
//            dataSetName =  dataSetManager.getDataSet(adapterDataSet,innerVersion).getName();
//            AdapterCustomize parent = new AdapterCustomize();
//            parent.setId("adapterDataSet"+adapterDataSet);
//            parent.setPid("adapter0");
//            parent.setText(dataSetName);
//            parent.setIschecked(true);
//            adapterCustomizeList.add(parent);
//            adapter=true;
//        }
//        //数据�?
//        for(XAdapterDataSet adapterDataSet:adapterMetaDataList){
//            metaDataName =  metaDataManager.getMetaData(dataSetManager.getDataSet(adapterDataSet.getDataSetId(),innerVersion),adapterDataSet.getMetaDataId()).getName();
//            AdapterCustomize child = new AdapterCustomize();
//            child.setId("adapterMetaData"+adapterDataSet.getMetaDataId());
//            child.setPid("adapterDataSet"+adapterDataSet.getDataSetId());
//            child.setText(metaDataName);
//            child.setIschecked(true);
//            adapterCustomizeList.add(child);
//            adapter=true;
//        }
//        //根节�?
//        if (adapter){
//            AdapterCustomize adapterRoot = new AdapterCustomize();
//            adapterRoot.setId("adapter0");
//            adapterRoot.setPid("-1");
//            adapterRoot.setText("数据�?");
//            adapterRoot.setIschecked(true);
//            adapterCustomizeList.add(adapterRoot);
//        }
//
//        //获取�?有标准数据集
//        XDataSet[] dataSetList = dataSetManager.getDataSetList(0, 0, cdaVersionManager.getVersion(version));
//        List<AdapterCustomize> stdCustomizeList = new ArrayList<>();
//        for(XDataSet dataSet:dataSetList){
//            AdapterCustomize parent = new AdapterCustomize();
//            parent.setId("stdDataSet"+dataSet.getId());
//            parent.setPid("std0");
//            parent.setText(dataSet.getName());
//            std=true;
//            childCheckCount=0;
//            List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSet);
//            for (XMetaData metaData:metaDataList) {
//                id = String.valueOf(metaData.getId());
//                check=false;
//                for(AdapterCustomize adapterCustomize:adapterCustomizeList){
//                    //已�?�配的要勾�??
//                    if(("adapterMetaData"+id).equals(adapterCustomize.getId())){
//                        check = true;
//                        childCheckCount++;
//                        break;
//                    }else{
//                        check = false;
//                    }
//                }
//                AdapterCustomize child = new AdapterCustomize();
//                child.setId("stdMetaData"+id);
//                child.setPid("stdDataSet"+dataSet.getId());
//                child.setText(metaData.getName());
//                stdCustomizeList.add(child);
//                child.setIschecked(check);
//                std=true;
//            }
//            if (metaDataList.size()==childCheckCount && childCheckCount>0){
//                parent.setIschecked(true);//子节点全�?
//            }
//            stdCustomizeList.add(parent);
//        }
//        //根节�?
//        if (std){
//            AdapterCustomize stdRoot = new AdapterCustomize();
//            stdRoot.setId("std0");
//            stdRoot.setPid("-1");
//            stdRoot.setText("数据�?");
//            stdCustomizeList.add(stdRoot);
//        }
//        ObjectMapper mapper = new ObjectMapper();
//
//        model.addAttribute("planId",planId);
//        model.addAttribute("stdDataSet",mapper.writeValueAsString(stdCustomizeList).replace("'","\\'"));
//        model.addAttribute("adapterDataSet",mapper.writeValueAsString(adapterCustomizeList).replace("'","\\'"));
//        model.addAttribute("contentPage","adapter/adapterCustomize");
//        return "generalView";
    }

    /**
     * 定制数据�?
     * 2015-12-31  定制速度优化以及添加事务控制 by lincl
     * @param planId
     * @param customizeData
     * @return
     */
    @RequestMapping("adapterDataSet")
    @ResponseBody
    public String adapterDataSet(Long planId,String customizeData) {
        customizeData=customizeData.replace("DataSet","").replace("MetaData","");

        String url = "/plan/adapterDataSet";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("planId",planId);
        params.put("customizeData",customizeData);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            customizeData=customizeData.replace("DataSet","").replace("MetaData","");
//            ObjectMapper mapper = new ObjectMapper();
//            List<AdapterCustomize> adapterDataSetList = Arrays.asList(mapper.readValue(customizeData,AdapterCustomize[].class));
////            JSONArray data = JSONArray.fromObject(customizeData);
////            List<AdapterCustomize> adapterDataSetList = (List)data.toCollection(data,AdapterCustomize.class);
//            orgAdapterPlanManager.adapterDataSet(planId, adapterDataSetList);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

}
