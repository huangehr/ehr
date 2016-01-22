package com.yihu.ehr.adaption.controller;

import com.yihu.ha.adapter.model.*;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.dict.model.common.AdapterType;
import com.yihu.ha.dict.model.common.XConventionalDictEntry;
import com.yihu.ha.std.model.*;
import com.yihu.ha.util.controller.BaseController;
import com.yihu.ha.util.operator.StringUtil;
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

    @RequestMapping("initial")
    public String orgAdapterInit(Model model) {
        model.addAttribute("contentPage","/adapter/adapter");
        return "pageView";
    }

    @RequestMapping("searchAdapterPlan")
    @ResponseBody
    //适配方案搜索
    public String searchAdapterPlan(String searchNm,String searchType,String searchOrg, int page, int rows){
        Result result = new Result();
        try {
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("code", searchNm);
            conditionMap.put("name", searchNm);
            conditionMap.put("org", searchOrg);
            conditionMap.put("page", page);
            conditionMap.put("pageSize", rows);
            List<AdapterType> typeLs = new ArrayList<>();
            if (StringUtil.isEmpty(searchType)){
                typeLs.add(conventionalDictEntry.getAdapterType("1"));
                typeLs.add(conventionalDictEntry.getAdapterType("2"));
                typeLs.add(conventionalDictEntry.getAdapterType("3"));
            }else{
                typeLs.add(conventionalDictEntry.getAdapterType(searchType));
            }
            conditionMap.put("typeLs", typeLs);

            List<AdapterPlanBrowserModel> orgAdapterPlans = orgAdapterPlanManager.searchAdapterPlanBrowser(conditionMap);
            Integer totalCount = orgAdapterPlanManager.searchOrgAdapterPlanInt(conditionMap);
            result = getResult(orgAdapterPlans, totalCount, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("getAdapterPlan")
    //获取适配方案信息
    public String getAdapterPlan(Model model, Long id, String mode){
        AdapterPlanModel planModel=null;
        if (id!=null){
            //修改
            XOrgAdapterPlan orgAdapterPlan  = orgAdapterPlanManager.getOrgAdapterPlan(id);
            planModel = orgAdapterPlanManager.getOrgAdapterPlan(orgAdapterPlan);
        }
        model.addAttribute("adapterPlan",planModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","adapter/adapterInfoDialog");
        return "simpleView";
    }

    @RequestMapping("getAdapterPlanById")
    @ResponseBody
    public String getAdapterPlanById(Long id) {
        Result result = new Result();
        try{
            XOrgAdapterPlan orgAdapterPlan  = orgAdapterPlanManager.getOrgAdapterPlan(id);
            AdapterPlanModel planModel = orgAdapterPlanManager.getOrgAdapterPlan(orgAdapterPlan);
            result.setObj(planModel);
            result.setSuccessFlg(true);
        }catch (Exception es){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    private XOrgAdapterPlan getOrgAdapterPlan(Long id) {
        return id==null? new OrgAdapterPlan(): orgAdapterPlanManager.getOrgAdapterPlan(id);
    }
    @RequestMapping("updateAdapterPlan")
    @ResponseBody
    //更新适配方案
    public String updateAdapterPlan(AdapterPlanModel adapterPlanModel){
        try {
            Result result = new Result();
            XOrgAdapterPlan orgAdapterPlan = getOrgAdapterPlan(adapterPlanModel.getId());
            boolean checkCode = true;
            if(adapterPlanModel.getId()!=null && adapterPlanModel.getCode().equals(orgAdapterPlan.getCode()))
                checkCode=false;
            if(checkCode && orgAdapterPlanManager.isAdapterCodeExist(adapterPlanModel.getCode())){
                result.setErrorMsg("codeNotUnique");
                return result.toJson();
            }
            orgAdapterPlan.setCode(adapterPlanModel.getCode());
            orgAdapterPlan.setName(adapterPlanModel.getName());
            orgAdapterPlan.setDescription(adapterPlanModel.getDescription());
            orgAdapterPlan.setVersion(cdaVersionManager.getVersion(adapterPlanModel.getVersion()));
            orgAdapterPlan.setType(conventionalDictEntry.getAdapterType(adapterPlanModel.getType()));
            orgAdapterPlan.setOrg(adapterPlanModel.getOrg());
            orgAdapterPlan.setParentId(adapterPlanModel.getParentId());
            orgAdapterPlan.setOrg(adapterPlanModel.getOrg());
            orgAdapterPlan.setParentId(adapterPlanModel.getParentId());
            if (adapterPlanModel.getId()==null){
                orgAdapterPlanManager.addOrgAdapterPlan(orgAdapterPlan);
            }else{
                orgAdapterPlanManager.updateOrgAdapterPlan(orgAdapterPlan);
            }

            result = getSuccessResult(true);
            return result.toJson();
        }catch (Exception e){
            Result result = getSuccessResult(false);
            return result.toJson();
        }
    }

    @RequestMapping("orgIsExistData")
    @ResponseBody
    public String orgIsExistData(String org){
        Result result = new Result();
        try {
            boolean rtn = adapterOrgManager.isExistData(org);
            result.setSuccessFlg(rtn);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("delAdapterPlan")
    @ResponseBody
    //删除适配方案
    public String delAdapterPlan(@RequestParam("id[]") Long[] id){
        int rtn = orgAdapterPlanManager.deleteOrgAdapterPlan(id);
        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
        return result.toJson();
    }

    @RequestMapping("getAdapterPlanList")
    @ResponseBody
    //获取适配方案列表
    public String getAdapterPlanList(String type){
        Result result = new Result();
        try {
            //根据类型获取所有方案
            List<XOrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.getOrgAdapterPlan(type);
            List<Map> adapterPlan = new ArrayList<>();
            if (!orgAdapterPlans.isEmpty()){
                for (XOrgAdapterPlan plan : orgAdapterPlans) {
                    Map<String,String> map = new HashMap<>();
                    map.put("code",plan.getId().toString());
                    map.put("value",plan.getName());
                    adapterPlan.add(map);
                }
            }
            result.setDetailModelList(adapterPlan);
            result.setSuccessFlg(true);
        } catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("getOrgList")
    @ResponseBody
    //获取适配方案列表
    public String getOrgList(Model model, String type) {
        //根据类型获取所有采集标准
        Result result = new Result();
        try {
            List<XAdapterOrg> adapterOrgList=null;
            adapterOrgList=adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type));

            List<Map> adapterOrgs = new ArrayList<>();

            if (!adapterOrgList.isEmpty()){
                for (XAdapterOrg adapterOrg : adapterOrgList) {
                    Map<String,String> map = new HashMap<>();
                    map.put("code",adapterOrg.getCode());
                    map.put("value",adapterOrg.getName());
                    adapterOrgs.add(map);
                }
            }
            result.setSuccessFlg(true);
            result.setDetailModelList(adapterOrgs);
        } catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("getAdapterCustomize")
    public String getAdapterCustomize(Model model, Long planId, String version) throws IOException {
        String dataSetName,metaDataName;
        String id;
        boolean check=true;     //是否勾选
        boolean std=false;      //标准是否添加根节点
        boolean adapter=false;  //定制是否添加根节点
        long childCheckCount;
        //获取所有定制数据集
        List<Long> adapterDataSetList = orgAdapterPlanManager.getAdapterDataSet(planId);
        List<XAdapterDataSet> adapterMetaDataList = orgAdapterPlanManager.getAdapterMetaData(planId);
        XCDAVersion innerVersion = cdaVersionManager.getVersion(version);
        List<AdapterCustomize> adapterCustomizeList = new ArrayList<>();
        //数据集
        for(Long adapterDataSet:adapterDataSetList){
            dataSetName =  dataSetManager.getDataSet(adapterDataSet,innerVersion).getName();
            AdapterCustomize parent = new AdapterCustomize();
            parent.setId("adapterDataSet"+adapterDataSet);
            parent.setPid("adapter0");
            parent.setText(dataSetName);
            parent.setIschecked(true);
            adapterCustomizeList.add(parent);
            adapter=true;
        }
        //数据元
        for(XAdapterDataSet adapterDataSet:adapterMetaDataList){
            metaDataName =  metaDataManager.getMetaData(dataSetManager.getDataSet(adapterDataSet.getDataSetId(),innerVersion),adapterDataSet.getMetaDataId()).getName();
            AdapterCustomize child = new AdapterCustomize();
            child.setId("adapterMetaData"+adapterDataSet.getMetaDataId());
            child.setPid("adapterDataSet"+adapterDataSet.getDataSetId());
            child.setText(metaDataName);
            child.setIschecked(true);
            adapterCustomizeList.add(child);
            adapter=true;
        }
        //根节点
        if (adapter){
            AdapterCustomize adapterRoot = new AdapterCustomize();
            adapterRoot.setId("adapter0");
            adapterRoot.setPid("-1");
            adapterRoot.setText("数据集");
            adapterRoot.setIschecked(true);
            adapterCustomizeList.add(adapterRoot);
        }

        //获取所有标准数据集
        XDataSet[] dataSetList = dataSetManager.getDataSetList(0, 0, cdaVersionManager.getVersion(version));
        List<AdapterCustomize> stdCustomizeList = new ArrayList<>();
        for(XDataSet dataSet:dataSetList){
            AdapterCustomize parent = new AdapterCustomize();
            parent.setId("stdDataSet"+dataSet.getId());
            parent.setPid("std0");
            parent.setText(dataSet.getName());
            std=true;
            childCheckCount=0;
            List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSet);
            for (XMetaData metaData:metaDataList) {
                id = String.valueOf(metaData.getId());
                check=false;
                for(AdapterCustomize adapterCustomize:adapterCustomizeList){
                    //已适配的要勾选
                    if(("adapterMetaData"+id).equals(adapterCustomize.getId())){
                        check = true;
                        childCheckCount++;
                        break;
                    }else{
                        check = false;
                    }
                }
                AdapterCustomize child = new AdapterCustomize();
                child.setId("stdMetaData"+id);
                child.setPid("stdDataSet"+dataSet.getId());
                child.setText(metaData.getName());
                stdCustomizeList.add(child);
                child.setIschecked(check);
                std=true;
            }
            if (metaDataList.size()==childCheckCount && childCheckCount>0){
                parent.setIschecked(true);//子节点全选
            }
            stdCustomizeList.add(parent);
        }
        //根节点
        if (std){
            AdapterCustomize stdRoot = new AdapterCustomize();
            stdRoot.setId("std0");
            stdRoot.setPid("-1");
            stdRoot.setText("数据集");
            stdCustomizeList.add(stdRoot);
        }
        ObjectMapper mapper = new ObjectMapper();

        model.addAttribute("planId",planId);
        model.addAttribute("stdDataSet",mapper.writeValueAsString(stdCustomizeList).replace("'","\\'"));
        model.addAttribute("adapterDataSet",mapper.writeValueAsString(adapterCustomizeList).replace("'","\\'"));
        model.addAttribute("contentPage","adapter/adapterCustomize");
        return "generalView";
    }

    @RequestMapping("adapterDataSet")
    @ResponseBody
    //定制数据集
    public String adapterDataSet(Long planId,String customizeData) {
        Result result = new Result();
        try {
            customizeData=customizeData.replace("DataSet","").replace("MetaData","");
            ObjectMapper mapper = new ObjectMapper();
            List<AdapterCustomize> adapterDataSetList = Arrays.asList(mapper.readValue(customizeData,AdapterCustomize[].class));
//            JSONArray data = JSONArray.fromObject(customizeData);
//            List<AdapterCustomize> adapterDataSetList = (List)data.toCollection(data,AdapterCustomize.class);
            orgAdapterPlanManager.adapterDataSet(planId, adapterDataSetList);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

}
