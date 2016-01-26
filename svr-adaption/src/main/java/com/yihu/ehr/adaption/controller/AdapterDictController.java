package com.yihu.ehr.adaption.controller;

import com.yihu.ehr.adaption.service.AdapterDict;
import com.yihu.ehr.adaption.service.AdapterDictModel;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zqb on 2015/11/13.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterDict")
@Api(protocols = "https", value = "adapterDict", description = "适配字典管理接口", tags = {"适配字典管理"})
public class AdapterDictController extends BaseRestController {
//    @Resource(name = Services.OrgAdapterPlanManager)
//    XOrgAdapterPlanManager orgAdapterPlanManager;
//
//    @Resource(name=Services.AdapterDictManager)
//    private XAdapterDictManager adapterDictManager;
//
//    @Resource(name = Services.OrgDictManager)
//    private XOrgDictManager orgDictManager;
//
//    @Resource(name = Services.OrgDictItemManager)
//    private XOrgDictItemManager orgDictItemManager;
//
//
//    @Resource(name=Services.DictManager)
//    private XDictManager dictManager;


    /**
     * 根据方案ID及查询条件查询字典适配关系
     * @param adapterPlanId
     * @param strKey
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/adapterDicts" , method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询字典适配关系")
    public Object searchAdapterDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "strKey", value = "strKey", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows){
//        Result result = new Result();
//
//        try {
//            List<AdapterDictModel> dict = adapterDictManager.searchAdapterDict(adapterPlanId, strKey, page, rows);
//            int totalCount = adapterDictManager.searchDictInt(adapterPlanId, strKey);
//            result.setSuccessFlg(true);
//            result = getResult(dict,totalCount,page,rows);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 根据dictId搜索字典项适配关系
     * @param adapterPlanId
     * @param dictId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/adapterDictEntrys" , method = RequestMethod.GET)
    @ApiOperation(value = "根据dictId搜索字典项适配关系")
    public String searchAdapterDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "dictId", value = "dictId", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "strKey", value = "strKey", defaultValue = "")
            @RequestParam(value = "strKey") Long strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows){
//        Result result = new Result();
//
//        List<AdapterDictModel> adapterDictModels;
//        int totalCount;
//        try {
//            adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, strKey, page, rows);
//            totalCount = adapterDictManager.searchDictEntryInt(adapterPlanId, dictId, strKey);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
//        result.setSuccessFlg(true);
//        result = getResult(adapterDictModels,totalCount,page,rows);
//        return result.toJson();
        return null;
    }

    /**
     * 根据字典ID获取字典项适配关系明细
     * @param id
     * @return
     */
    @RequestMapping(value = "/adapterDictEntry" , method = RequestMethod.GET)
    @ApiOperation(value = "根据字典ID获取字典项适配关系明细")
    public String getAdapterDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id){
//        Result result = new Result();
//        try {
//            XAdapterDict adapterDict = adapterDictManager.getAdapterDict(id);
//
//            result.setObj(adapterDict);
//            result.setSuccessFlg(true);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
        return null;
    }


    /**
     * 根据字典项ID获取字典项
     * @param id
     * @return
     */
    @RequestMapping(value = "/adapterDict" , method = RequestMethod.GET)
    @ApiOperation(value = "根据字典项ID获取字典项")
    private AdapterDict getAdapterDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id) {
//        return id==null? new AdapterDict(): adapterDictManager.getAdapterDict(id);
        return null;
    }

    /**
     * 修改字典项映射关系
     * @param
     * @return
     */
    @RequestMapping(value = "/adapterDictEntry" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项映射关系")
    public String updateAdapterDictEntry(AdapterDictModel adapterDictModel){
//        Result result = new Result();
//        try {
//            XAdapterDict adapterDict = getAdapterDict(adapterDictModel.getId());
//            adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
//            adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
//            adapterDict.setDictId(adapterDictModel.getDictId());
//            adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
//            adapterDict.setOrgDictItemSeq(adapterDictModel.getOrgDictEntrySeq());
//            adapterDict.setDescription(adapterDictModel.getDescription());
//            if (adapterDictModel.getId()==null){
//                adapterDictManager.addAdapterDict(adapterDict);
//            }else {
//                adapterDictManager.updateAdapterDict(adapterDict);
//            }
//            result.setSuccessFlg(true);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 删除字典项映射的方法
     * @param id
     * @return
     */
    @RequestMapping(value = "/dictEntry" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项映射的方法")
    public String delDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long[] id){
//        int rtn = adapterDictManager.deleteAdapterDict(id);
//        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
//        return result.toJson();
        return null;
    }

    /**
     * 标准字典项的下拉
     * @return
     */
    @RequestMapping(value = "/stdDictEntry" , method = RequestMethod.GET)
    @ApiOperation(value = "标准字典项的下拉")
    public Object getStdDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "dictId", value = "dictId", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "mode", value = "mode", defaultValue = "")
            @RequestParam(value = "mode") String mode){
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            XCDAVersion version = orgAdapterPlan.getVersion();
//            List<XDictEntry> dictEntryList = Arrays.asList(dictManager.getDictEntries(dictManager.getDict(dictId, version)));
//            List<String> dictEntries = new ArrayList<>();
//            if (!dictEntryList.isEmpty()){
//                if("modify".equals(mode) || "view".equals(mode)){
//                    for (XDictEntry dictEntry : dictEntryList) {
//                        dictEntries.add(String.valueOf(dictEntry.getId())+','+dictEntry.getValue());
//                    }
//                }
//                else{
//                    List<AdapterDictModel> adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, "", 0, 0);
//                    boolean exist = false;
//                    for (XDictEntry dictEntry : dictEntryList) {
//                        exist = false;
//                        for(AdapterDictModel model : adapterDictModels){
//                            if(dictEntry.getId()==model.getDictEntryId()){
//                                exist = true;
//                                break;
//                            }
//                        }
//                        if(!exist)
//                            dictEntries.add(String.valueOf(dictEntry.getId())+','+dictEntry.getValue());
//                    }
//                }
//
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(dictEntries);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 机构字典下拉
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.GET)
    @ApiOperation(value = "机构字典下拉")
    public Object getOrgDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId){
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            String orgCode = orgAdapterPlan.getOrg();
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            List<XOrgDict>  orgDictList = orgDictManager.searchOrgDict(conditionMap);
//            List<String> orgDicts = new ArrayList<>();
//            if (!orgDictList.isEmpty()){
//                for (XOrgDict orgDict : orgDictList) {
//                    orgDicts.add(String.valueOf(orgDict.getId())+','+orgDict.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgDicts);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 机构字典项下拉
     * @param orgDictSeq
     * @return
     */
    @RequestMapping(value = "/orgDictEntry" , method = RequestMethod.GET)
    @ApiOperation(value = "机构字典项下拉")
    public Object getOrgDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId){
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            String orgCode = orgAdapterPlan.getOrg();
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgDictSeq", orgDictSeq);
//            conditionMap.put("orgCode", orgCode);
//            List<XOrgDictItem> orgDictItemList = orgDictItemManager.searchOrgDictItem(conditionMap);
//            List<String> orgDictItems = new ArrayList<>();
//            if (!orgDictItemList.isEmpty()){
//                for (XOrgDictItem orgDictItem : orgDictItemList) {
//                    orgDictItems.add(String.valueOf(orgDictItem.getSequence())+','+orgDictItem.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgDictItems);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }
}
