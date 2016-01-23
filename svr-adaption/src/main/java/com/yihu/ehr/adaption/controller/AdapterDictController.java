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
import java.util.*;

/**
 * Created by zqb on 2015/11/13.
 */
@RequestMapping("/adapterDict")
@Controller
public class AdapterDictController extends BaseController {
    @Resource(name = Services.OrgAdapterPlanManager)
    XOrgAdapterPlanManager orgAdapterPlanManager;

    @Resource(name=Services.AdapterDictManager)
    private XAdapterDictManager adapterDictManager;

    @Resource(name = Services.OrgDictManager)
    private XOrgDictManager orgDictManager;

    @Resource(name = Services.OrgDictItemManager)
    private XOrgDictItemManager orgDictItemManager;


    @Resource(name=Services.DictManager)
    private XDictManager dictManager;

    @RequestMapping("template/adapterMetaDataInfo")
    public String adapterMetaDataInfoTemplate(Model model, Long id, String mode) {
        XAdapterDict adapterDict = new AdapterDict();
        if(mode.equals("view") || mode.equals("modify")){
            try {
                adapterDict = adapterDictManager.getAdapterDict(id);
                model.addAttribute("rs", "success");
            } catch (Exception e) {
                model.addAttribute("rs", "error");
            }
        }
        model.addAttribute("info", adapterDict);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/adapterDataSet/dictDialog");
        return "generalView";
    }

    /**
     * 根据方案ID及查询条件查询字典适配关系
     * @param adapterPlanId
     * @param strKey
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchAdapterDict")
    @ResponseBody
    public String searchAdapterDict(Long adapterPlanId, String strKey,int page, int rows){
        Result result = new Result();

        try {
            List<AdapterDictModel> dict = adapterDictManager.searchAdapterDict(adapterPlanId, strKey, page, rows);
            int totalCount = adapterDictManager.searchDictInt(adapterPlanId, strKey);
            result.setSuccessFlg(true);
            result = getResult(dict,totalCount,page,rows);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
    }

    /**
     * 根据dictId搜索字典项适配关系
     * @param adapterPlanId
     * @param dictId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchAdapterDictEntry")
    @ResponseBody
    public String searchAdapterDictEntry(Long adapterPlanId, Long dictId,String strKey,int page, int rows){
        Result result = new Result();

        List<AdapterDictModel> adapterDictModels;
        int totalCount;
        try {
            adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, strKey, page, rows);
            totalCount = adapterDictManager.searchDictEntryInt(adapterPlanId, dictId, strKey);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        result.setSuccessFlg(true);
        result = getResult(adapterDictModels,totalCount,page,rows);
        return result.toJson();
    }

    /**
     * 根据字典ID获取字典项适配关系明细
     * @param id
     * @return
     */
    @RequestMapping("/getAdapterDictEntry")
    @ResponseBody
    public String getAdapterDictEntry(Long id){
        Result result = new Result();
        try {
            XAdapterDict adapterDict = adapterDictManager.getAdapterDict(id);

            result.setObj(adapterDict);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }

    }

    private XAdapterDict getAdapterDict(Long id) {
        return id==null? new AdapterDict(): adapterDictManager.getAdapterDict(id);
    }

    /**
     * 修改字典项映射关系
     * @param
     * @return
     */
    @RequestMapping("/updateAdapterDictEntry")
    @ResponseBody
    public String updateAdapterDictEntry(AdapterDictModel adapterDictModel){
        Result result = new Result();
        try {
            XAdapterDict adapterDict = getAdapterDict(adapterDictModel.getId());
            adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
            adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
            adapterDict.setDictId(adapterDictModel.getDictId());
            adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
            adapterDict.setOrgDictItemSeq(adapterDictModel.getOrgDictEntrySeq());
            adapterDict.setDescription(adapterDictModel.getDescription());
            if (adapterDictModel.getId()==null){
                adapterDictManager.addAdapterDict(adapterDict);
            }else {
                adapterDictManager.updateAdapterDict(adapterDict);
            }
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 删除字典项映射的方法
     * @param id
     * @return
     */
    @RequestMapping("/delDictEntry")
    @ResponseBody
    public String delDictEntry(@RequestParam("id") Long[] id){
        int rtn = adapterDictManager.deleteAdapterDict(id);
        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
        return result.toJson();
    }

    /**
     * 标准字典项的下拉
     * @return
     */
    @RequestMapping("/getStdDictEntry")
    @ResponseBody
    public Object getStdDictEntry(Long adapterPlanId,Long dictId,String mode){
        Result result = new Result();
        try {
            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
            XCDAVersion version = orgAdapterPlan.getVersion();
            List<XDictEntry> dictEntryList = Arrays.asList(dictManager.getDictEntries(dictManager.getDict(dictId, version)));
            List<String> dictEntries = new ArrayList<>();
            if (!dictEntryList.isEmpty()){
                if("modify".equals(mode) || "view".equals(mode)){
                    for (XDictEntry dictEntry : dictEntryList) {
                        dictEntries.add(String.valueOf(dictEntry.getId())+','+dictEntry.getValue());
                    }
                }
                else{
                    List<AdapterDictModel> adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, "", 0, 0);
                    boolean exist = false;
                    for (XDictEntry dictEntry : dictEntryList) {
                        exist = false;
                        for(AdapterDictModel model : adapterDictModels){
                            if(dictEntry.getId()==model.getDictEntryId()){
                                exist = true;
                                break;
                            }
                        }
                        if(!exist)
                            dictEntries.add(String.valueOf(dictEntry.getId())+','+dictEntry.getValue());
                    }
                }

                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            result.setObj(dictEntries);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 机构字典下拉
     * @return
     */
    @RequestMapping("/getOrgDict")
    @ResponseBody
    public Object getOrgDict(Long adapterPlanId){
        Result result = new Result();
        try {
            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
            String orgCode = orgAdapterPlan.getOrg();
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgCode", orgCode);
            List<XOrgDict>  orgDictList = orgDictManager.searchOrgDict(conditionMap);
            List<String> orgDicts = new ArrayList<>();
            if (!orgDictList.isEmpty()){
                for (XOrgDict orgDict : orgDictList) {
                    orgDicts.add(String.valueOf(orgDict.getId())+','+orgDict.getName());
                }
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            result.setObj(orgDicts);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 机构字典项下拉
     * @param orgDictSeq
     * @return
     */
    @RequestMapping("/getOrgDictEntry")
    @ResponseBody
    public Object getOrgDictEntry(Integer orgDictSeq,Long adapterPlanId){
        Result result = new Result();
        try {
            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
            String orgCode = orgAdapterPlan.getOrg();
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgDictSeq", orgDictSeq);
            conditionMap.put("orgCode", orgCode);
            List<XOrgDictItem> orgDictItemList = orgDictItemManager.searchOrgDictItem(conditionMap);
            List<String> orgDictItems = new ArrayList<>();
            if (!orgDictItemList.isEmpty()){
                for (XOrgDictItem orgDictItem : orgDictItemList) {
                    orgDictItems.add(String.valueOf(orgDictItem.getSequence())+','+orgDictItem.getName());
                }
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            result.setObj(orgDictItems);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }
}
