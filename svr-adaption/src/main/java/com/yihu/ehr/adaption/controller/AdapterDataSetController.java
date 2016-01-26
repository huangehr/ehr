package com.yihu.ehr.adaption.controller;

import com.yihu.ehr.adaption.service.AdapterDataSet;
import com.yihu.ehr.adaption.service.AdapterDataSetModel;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 适配管理方案适配管理
 * Created by wq on 2015/11/1.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterDataSet")
@Api(protocols = "https", value = "patient", description = "卡管理接口", tags = {"病人对卡的认领等操作"})
public class AdapterDataSetController extends BaseRestController{
//    @Resource(name = Services.OrgAdapterPlanManager)
//    XOrgAdapterPlanManager orgAdapterPlanManager;
//
//    @Resource(name=Services.AdapterDataSetManager)
//    private XAdapterDataSetManager adapterDataSetManager;
//
//    @Resource(name = Services.OrgDataSetManager)
//    private XOrgDataSetManager orgDataSetManager;
//
//    @Resource(name = Services.OrgMetaDataManager)
//     private XOrgMetaDataManager orgMetaDataManager;
//
//    @Resource(name= Services.DataSetManager)
//    private XDataSetManager dataSetManager;
//
//    @Resource(name=Services.MetaDataManager)
//    private XMetaDataManager metaDataManager;


    /**
     * 根据方案ID及查询条件查询数据集适配关系
     * @param adapterPlanId
     * @param strKey
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/adapterDataSet")
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Object searchAdapterDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "strKey", value = "查询条件", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") String page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") String rows){
//        Result result = new Result();
//
//        try {
//            List<DataSetModel> dataSet = adapterDataSetManager.searchAdapterDataSet(adapterPlanId, strKey, page, rows);
//            int totalCount = adapterDataSetManager.searchDataSetInt(adapterPlanId, strKey);
//            result.setSuccessFlg(true);
//            result = getResult(dataSet,totalCount,page,rows);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 根据dataSetId搜索数据元适配关系
     * @param adapterPlanId
     * @param dataSetId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/adapterMetaData/dataSet_id")
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Object searchAdapterMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "dataSetId", value = "dataSetId", defaultValue = "")
            @RequestParam(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "strKey", value = "查询条件", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") String page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") String rows){
//        Result result = new Result();
//
//        List<AdapterDataSetModel> adapterDataSetModels;
//        int totalCount;
//        try {
//            adapterDataSetModels = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, strKey, page, rows);
//            totalCount = adapterDataSetManager.searchMetaDataInt(adapterPlanId, dataSetId,strKey);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
//        result.setSuccessFlg(true);
//        result = getResult(adapterDataSetModels,totalCount,page,rows);
//        return result.toJson();
        return null;
    }

    /**
     * 根据数据集ID获取数据元适配关系明细
     * @param id
     * @return
     */
    @RequestMapping("/adapterMetaData")
    @ApiOperation(value = "根据数据集ID获取数据元适配关系明细")
    public Object getAdapterMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id){
//        Result result = new Result();
//        try {
//            XAdapterDataSet adapterDataSet = adapterDataSetManager.getAdapterMetaData(id);
//
//            result.setObj(adapterDataSet);
//            result.setSuccessFlg(true);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
        return null;

    }


    /**
     * 根据数据元ID获取数据集
     * @param id
     * @return
     */
    @RequestMapping("/adapterDataSet")
    @ApiOperation(value = "根据数据元ID获取数据集")
    private AdapterDataSet getAdapterDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        //return id==null? new AdapterDataSet(): adapterDataSetManager.getAdapterMetaData(id);
        return null;
    }

    /**
     * 修改数据元映射关系
     * @param
     * @return
     */
    @RequestMapping(value = "/adapterMetaData" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元映射关系")
    public Object updateAdapterMetaData(AdapterDataSetModel adapterDataSetModel){
//        Result result = new Result();
//        try {
//            XAdapterDataSet adapterDataSet = getAdapterDataSet(adapterDataSetModel.getId());
//            adapterDataSet.setAdapterPlanId(adapterDataSetModel.getAdapterPlanId());
//            adapterDataSet.setMetaDataId(adapterDataSetModel.getMetaDataId());
//            adapterDataSet.setDataSetId(adapterDataSetModel.getDataSetId());
//            adapterDataSet.setOrgDataSetSeq(adapterDataSetModel.getOrgDataSetSeq());
//            adapterDataSet.setOrgMetaDataSeq(adapterDataSetModel.getOrgMetaDataSeq());
//            adapterDataSet.setDataType(adapterDataSetModel.getDataTypeCode());
//            adapterDataSet.setDescription(adapterDataSetModel.getDescription());
//            if (adapterDataSetModel.getId()==null){
//                adapterDataSetManager.addAdapterDataSet(adapterDataSet);
//            }else {
//                adapterDataSetManager.updateAdapterDataSet(adapterDataSet);
//            }
//            result.setSuccessFlg(true);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 删除数据元映射的方法
     * @param id
     * @return
     */
    @RequestMapping(value = "/metaData" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元映射的方法")
    public Object delMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id){
//        int rtn = adapterDataSetManager.deleteAdapterDataSet(id);
//        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
//        return result.toJson();
        return null;
    }

    /**
     * 标准数据元的下拉
     * @return
     */
    @RequestMapping(value = "/stdMetaData" , method = RequestMethod.GET)
    @ApiOperation(value = "标准数据元的下拉")
    public Object getStdMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "id") Long adapterPlanId,
            @ApiParam(name = "id", value = "dataSetId", defaultValue = "")
            @RequestParam(value = "id") Long dataSetId,
            @ApiParam(name = "mode", value = "mode", defaultValue = "")
            @RequestParam(value = "mode") String mode){
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            XCDAVersion version = orgAdapterPlan.getVersion();
//            List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSetManager.getDataSet(dataSetId,version));
//            List<String> stdMetaData = new ArrayList<>();
//            if (!metaDataList.isEmpty()){
//                if("modify".equals(mode) || "view".equals(mode)){
//                    for (XMetaData metaData : metaDataList) {
//                        stdMetaData.add(String.valueOf(metaData.getId())+','+metaData.getName());
//                    }
//                }
//                else{
//                    List<AdapterDataSetModel> adapterDataSetModels = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, "", 0, 0);
//                    boolean exist = false;
//                    for (XMetaData metaData : metaDataList) {
//                        exist = false;
//                        for(AdapterDataSetModel model :adapterDataSetModels){
//                            if(model.getMetaDataId()==metaData.getId()){
//                                exist = true;
//                                break;
//                            }
//                        }
//                        if(!exist)
//                            stdMetaData.add(String.valueOf(metaData.getId())+','+metaData.getName());
//                    }
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(stdMetaData);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 机构数据集下拉
     * @return
     */
    @RequestMapping(value = "/orgDataSet" , method = RequestMethod.GET)
    @ApiOperation(value = "机构数据集下拉")
    public Object getOrgDataSet(
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
//            List<XOrgDataSet>  orgDataSetList = orgDataSetManager.searchOrgDataSet(conditionMap);
//            List<String> orgDataSets = new ArrayList<>();
//            if (!orgDataSetList.isEmpty()){
//                for (XOrgDataSet orgDataSet : orgDataSetList) {
//                    orgDataSets.add(String.valueOf(orgDataSet.getSequence())+','+orgDataSet.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgDataSets);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 机构数据元下拉
     * @param orgDataSetSeq
     * @return
     */
    @RequestMapping(value = "/orgMetaData" , method = RequestMethod.GET)
    @ApiOperation(value = "机构数据元下拉")
    public Object getOrgMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") Long orgDataSetSeq,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId){
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            String orgCode = orgAdapterPlan.getOrg();
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgDataSetSeq", orgDataSetSeq);
//            conditionMap.put("orgCode", orgCode);
//            List<XOrgMetaData> orgMetaDataList = orgMetaDataManager.searchOrgMetaData(conditionMap);
//            List<String> orgMetaDatas = new ArrayList<>();
//            if (!orgMetaDataList.isEmpty()){
//                for (XOrgMetaData orgMetaData : orgMetaDataList) {
//                    orgMetaDatas.add(String.valueOf(orgMetaData.getSequence())+','+orgMetaData.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgMetaDatas);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }
}
