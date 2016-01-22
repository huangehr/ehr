package com.yihu.ehr.adaption.controller;

import com.yihu.ha.adapter.model.*;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.std.model.*;
import com.yihu.ha.util.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 适配管理方案适配管理
 * Created by wq on 2015/11/1.
 */

@RequestMapping("/adapterDataSet")
@Controller
public class AdapterDataSetController extends BaseController{
    @Resource(name = Services.OrgAdapterPlanManager)
    XOrgAdapterPlanManager orgAdapterPlanManager;

    @Resource(name=Services.AdapterDataSetManager)
    private XAdapterDataSetManager adapterDataSetManager;

    @Resource(name = Services.OrgDataSetManager)
    private XOrgDataSetManager orgDataSetManager;

    @Resource(name = Services.OrgMetaDataManager)
     private XOrgMetaDataManager orgMetaDataManager;

    @Resource(name= Services.DataSetManager)
    private XDataSetManager dataSetManager;

    @Resource(name=Services.MetaDataManager)
    private XMetaDataManager metaDataManager;

    @RequestMapping("/initialOld")
    public String adapterDataSetOldInitial(HttpServletRequest request,String adapterPlanId){
        request.setAttribute("adapterPlanId",adapterPlanId);
        return "/adapter/adapterDataSet";
    }

    @RequestMapping("/initial")
    public String adapterDataSetInitial(Model model, String adapterPlanId){
        model.addAttribute("adapterPlanId",adapterPlanId);
        model.addAttribute("contentPage","/adapter/adapterDataSet/grid");
        return "pageView";
    }

    @RequestMapping("template/adapterMetaDataInfo")
    public String adapterMetaDataInfoTemplate(Model model, Long id, String mode) {
        XAdapterDataSet adapterDataSet = new AdapterDataSet();
        if(mode.equals("view") || mode.equals("modify")){
            try {
                adapterDataSet = adapterDataSetManager.getAdapterMetaData(id);
                model.addAttribute("rs", "success");
            } catch (Exception e) {
                model.addAttribute("rs", "error");
            }
        }
        model.addAttribute("info", adapterDataSet);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/adapterDataSet/dialog");
        return "generalView";
    }

    /**
     * 根据方案ID及查询条件查询数据集适配关系
     * @param adapterPlanId
     * @param strKey
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchAdapterDataSet")
    @ResponseBody
    public String searchAdapterDataSet(Long adapterPlanId, String strKey,int page, int rows){
        Result result = new Result();

        try {
            List<DataSetModel> dataSet = adapterDataSetManager.searchAdapterDataSet(adapterPlanId, strKey, page, rows);
            int totalCount = adapterDataSetManager.searchDataSetInt(adapterPlanId, strKey);
            result.setSuccessFlg(true);
            result = getResult(dataSet,totalCount,page,rows);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
    }

    /**
     * 根据dataSetId搜索数据元适配关系
     * @param adapterPlanId
     * @param dataSetId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchAdapterMetaData")
    @ResponseBody
    public String searchAdapterMetaData(Long adapterPlanId, Long dataSetId,String strKey,int page, int rows){
        Result result = new Result();

        List<AdapterDataSetModel> adapterDataSetModels;
        int totalCount;
        try {
            adapterDataSetModels = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, strKey, page, rows);
            totalCount = adapterDataSetManager.searchMetaDataInt(adapterPlanId, dataSetId,strKey);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        result.setSuccessFlg(true);
        result = getResult(adapterDataSetModels,totalCount,page,rows);
        return result.toJson();
    }

    /**
     * 根据数据集ID获取数据元适配关系明细
     * @param id
     * @return
     */
    @RequestMapping("/getAdapterMetaData")
    @ResponseBody
    public String getAdapterMetaData(Long id){
        Result result = new Result();
        try {
            XAdapterDataSet adapterDataSet = adapterDataSetManager.getAdapterMetaData(id);

            result.setObj(adapterDataSet);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }

    }

    private XAdapterDataSet getAdapterDataSet(Long id) {
        return id==null? new AdapterDataSet(): adapterDataSetManager.getAdapterMetaData(id);
    }

    /**
     * 修改数据元映射关系
     * @param
     * @return
     */
    @RequestMapping("/updateAdapterMetaData")
    @ResponseBody
    public String updateAdapterMetaData(AdapterDataSetModel adapterDataSetModel){
        Result result = new Result();
        try {
            XAdapterDataSet adapterDataSet = getAdapterDataSet(adapterDataSetModel.getId());
            adapterDataSet.setAdapterPlanId(adapterDataSetModel.getAdapterPlanId());
            adapterDataSet.setMetaDataId(adapterDataSetModel.getMetaDataId());
            adapterDataSet.setDataSetId(adapterDataSetModel.getDataSetId());
            adapterDataSet.setOrgDataSetSeq(adapterDataSetModel.getOrgDataSetSeq());
            adapterDataSet.setOrgMetaDataSeq(adapterDataSetModel.getOrgMetaDataSeq());
            adapterDataSet.setDataType(adapterDataSetModel.getDataTypeCode());
            adapterDataSet.setDescription(adapterDataSetModel.getDescription());
            if (adapterDataSetModel.getId()==null){
                adapterDataSetManager.addAdapterDataSet(adapterDataSet);
            }else {
                adapterDataSetManager.updateAdapterDataSet(adapterDataSet);
            }
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 删除数据元映射的方法
     * @param id
     * @return
     */
    @RequestMapping("/delMetaData")
    @ResponseBody
    public String delMetaData(@RequestParam("id") Long[] id){
        int rtn = adapterDataSetManager.deleteAdapterDataSet(id);
        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
        return result.toJson();
    }

    /**
     * 标准数据元的下拉
     * @return
     */
    @RequestMapping("/getStdMetaData")
    @ResponseBody
    public Object getStdMetaData(Long adapterPlanId,Long dataSetId,String mode){
        Result result = new Result();
        try {
            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
            XCDAVersion version = orgAdapterPlan.getVersion();
            List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSetManager.getDataSet(dataSetId,version));
            List<String> stdMetaData = new ArrayList<>();
            if (!metaDataList.isEmpty()){
                if("modify".equals(mode) || "view".equals(mode)){
                    for (XMetaData metaData : metaDataList) {
                        stdMetaData.add(String.valueOf(metaData.getId())+','+metaData.getName());
                    }
                }
                else{
                    List<AdapterDataSetModel> adapterDataSetModels = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, "", 0, 0);
                    boolean exist = false;
                    for (XMetaData metaData : metaDataList) {
                        exist = false;
                        for(AdapterDataSetModel model :adapterDataSetModels){
                            if(model.getMetaDataId()==metaData.getId()){
                                exist = true;
                                break;
                            }
                        }
                        if(!exist)
                            stdMetaData.add(String.valueOf(metaData.getId())+','+metaData.getName());
                    }
                }
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            result.setObj(stdMetaData);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 机构数据集下拉
     * @return
     */
    @RequestMapping("/getOrgDataSet")
    @ResponseBody
    public Object getOrgDataSet(Long adapterPlanId){
        Result result = new Result();
        try {
            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
            String orgCode = orgAdapterPlan.getOrg();
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgCode", orgCode);
            List<XOrgDataSet>  orgDataSetList = orgDataSetManager.searchOrgDataSet(conditionMap);
            List<String> orgDataSets = new ArrayList<>();
            if (!orgDataSetList.isEmpty()){
                for (XOrgDataSet orgDataSet : orgDataSetList) {
                    orgDataSets.add(String.valueOf(orgDataSet.getSequence())+','+orgDataSet.getName());
                }
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            result.setObj(orgDataSets);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 机构数据元下拉
     * @param orgDataSetSeq
     * @return
     */
    @RequestMapping("/getOrgMetaData")
    @ResponseBody
    public Object getOrgMetaData(Integer orgDataSetSeq,Long adapterPlanId){
        Result result = new Result();
        try {
            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
            String orgCode = orgAdapterPlan.getOrg();
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgDataSetSeq", orgDataSetSeq);
            conditionMap.put("orgCode", orgCode);
            List<XOrgMetaData> orgMetaDataList = orgMetaDataManager.searchOrgMetaData(conditionMap);
            List<String> orgMetaDatas = new ArrayList<>();
            if (!orgMetaDataList.isEmpty()){
                for (XOrgMetaData orgMetaData : orgMetaDataList) {
                    orgMetaDatas.add(String.valueOf(orgMetaData.getSequence())+','+orgMetaData.getName());
                }
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            result.setObj(orgMetaDatas);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }
}
