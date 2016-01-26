package com.yihu.ehr.adaption.controller;

import com.yihu.ehr.adaption.service.AdapterPlanModel;
import com.yihu.ehr.adaption.service.OrgAdapterPlan;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by zqb on 2015/10/27.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapter")
@Api(protocols = "https", value = "adapter", description = "适配器管理接口", tags = {"适配器管理"})
public class OrgAdapterPlanController extends BaseRestController {
//    @Resource(name = Services.OrgAdapterPlanManager)
//    private XOrgAdapterPlanManager orgAdapterPlanManager;
//
//    @Resource(name = Services.CDAVersionManager)
//    private XCDAVersionManager cdaVersionManager;
//
//    @Resource(name= Services.DataSetManager)
//    private XDataSetManager dataSetManager;
//
//    @Resource(name=Services.MetaDataManager)
//    private XMetaDataManager metaDataManager;
//
//    @Resource(name = Services.ConventionalDictEntry)
//    private XConventionalDictEntry conventionalDictEntry;
//    @Resource(name = Services.AdapterOrgManager)
//    private XAdapterOrgManager adapterOrgManager;

    public OrgAdapterPlanController(){}


    //适配方案搜索
    @RequestMapping(value = "/adapterPlan" , method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    public Object searchAdapterPlan(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "searchNm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "searchType", value = "类型", defaultValue = "")
            @RequestParam(value = "searchType") String searchType,
            @ApiParam(name = "searchOrg", value = "机构", defaultValue = "")
            @RequestParam(value = "searchOrg") String searchOrg,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows){
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
        return null;
    }

    @RequestMapping(value = "/adapterPlan" , method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    public Object getAdapterPlanById(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
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
        return null;
    }

    private OrgAdapterPlan getOrgAdapterPlan(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
//        return id==null? new OrgAdapterPlan(): orgAdapterPlanManager.getOrgAdapterPlan(id);
        return null;
    }

    //更新适配方案
    @RequestMapping(value = "/adapterPlan" , method = RequestMethod.PUT)
    @ApiOperation(value = "更新适配方案")
    public Object updateAdapterPlan(AdapterPlanModel adapterPlanModel){
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
//                orgAdapterPlanManager.addOrgAdapterPlan(orgAdapterPlan);
//            }else{
//                orgAdapterPlanManager.updateOrgAdapterPlan(orgAdapterPlan);
//            }
//
//            result = getSuccessResult(true);
//            return result.toJson();
//        }catch (Exception e){
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
        return null;
    }

    @RequestMapping(value = "/orgIsExistData" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否存在")
    public Object orgIsExistData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "org", value = "机构", defaultValue = "")
            @RequestParam(value = "org") String org){
//        Result result = new Result();
//        try {
//            boolean rtn = adapterOrgManager.isExistData(org);
//            result.setSuccessFlg(rtn);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    //删除适配方案
    @RequestMapping(value = "/adapterPlan" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案")
    public Object delAdapterPlan(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "ids", value = "编号列表", defaultValue = "")
            @RequestParam("ids") Long[] id){
//        int rtn = orgAdapterPlanManager.deleteOrgAdapterPlan(id);
//        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
//        return result.toJson();
        return null;
    }

    //获取适配方案列表
    @RequestMapping(value = "/adapterPlanList" , method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案列表")
    public Object getAdapterPlanList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam("type") String type){
//        Result result = new Result();
//        try {
//            //根据类型获取所有方案
//            List<XOrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.getOrgAdapterPlan(type);
//            List<Map> adapterPlan = new ArrayList<>();
//            if (!orgAdapterPlans.isEmpty()){
//                for (XOrgAdapterPlan plan : orgAdapterPlans) {
//                    Map<String,String> map = new HashMap<>();
//                    map.put("code",plan.getId().toString());
//                    map.put("value",plan.getName());
//                    adapterPlan.add(map);
//                }
//            }
//            result.setDetailModelList(adapterPlan);
//            result.setSuccessFlg(true);
//        } catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    //获取适配方案机构列表
    @RequestMapping(value = "/orgList" , method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案机构列表")
    public Object getOrgList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam("type") String type
            /*Model model, String type*/) {
        //根据类型获取所有采集标准
//        Result result = new Result();
//        try {
//            List<AdapterOrg> adapterOrgList=null;
//            adapterOrgList=adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type));
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
        return null;
    }

    @RequestMapping(value = "/adapterCustomize" , method = RequestMethod.GET)
    @ApiOperation(value = "")
    public Object getAdapterCustomize(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @RequestParam("planId") Long planId
            /*Model model, Long planId, String version*/) throws IOException {
//        String dataSetName,metaDataName;
//        String id;
//        boolean check=true;     //是否勾选
//        boolean std=false;      //标准是否添加根节点
//        boolean adapter=false;  //定制是否添加根节点
//        long childCheckCount;
//        //获取所有定制数据集
//        List<Long> adapterDataSetList = orgAdapterPlanManager.getAdapterDataSet(planId);
//        List<XAdapterDataSet> adapterMetaDataList = orgAdapterPlanManager.getAdapterMetaData(planId);
//        XCDAVersion innerVersion = cdaVersionManager.getVersion(version);
//        List<AdapterCustomize> adapterCustomizeList = new ArrayList<>();
//        //数据集
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
//        //数据元
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
//        //根节点
//        if (adapter){
//            AdapterCustomize adapterRoot = new AdapterCustomize();
//            adapterRoot.setId("adapter0");
//            adapterRoot.setPid("-1");
//            adapterRoot.setText("数据集");
//            adapterRoot.setIschecked(true);
//            adapterCustomizeList.add(adapterRoot);
//        }
//
//        //获取所有标准数据集
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
//                    //已适配的要勾选
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
//                parent.setIschecked(true);//子节点全选
//            }
//            stdCustomizeList.add(parent);
//        }
//        //根节点
//        if (std){
//            AdapterCustomize stdRoot = new AdapterCustomize();
//            stdRoot.setId("std0");
//            stdRoot.setPid("-1");
//            stdRoot.setText("数据集");
//            stdCustomizeList.add(stdRoot);
//        }
//        ObjectMapper mapper = new ObjectMapper();
//
//        model.addAttribute("planId",planId);
//        model.addAttribute("stdDataSet",mapper.writeValueAsString(stdCustomizeList).replace("'","\\'"));
//        model.addAttribute("adapterDataSet",mapper.writeValueAsString(adapterCustomizeList).replace("'","\\'"));
//        model.addAttribute("contentPage","adapter/adapterCustomize");
//        return "generalView";
        return null;
    }

    //定制数据集
    @RequestMapping(value = "/adapterDataSet" , method = RequestMethod.POST)
    @ApiOperation(value = "定制数据集")
    public Object adapterDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @RequestParam("planId") Long planId,
            @ApiParam(name = "customizeData", value = "customizeData", defaultValue = "")
            @RequestParam("customizeData") String customizeData) {
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
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

}
