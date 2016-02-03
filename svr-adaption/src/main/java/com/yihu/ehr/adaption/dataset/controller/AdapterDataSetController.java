package com.yihu.ehr.adaption.dataset.controller;

import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetManager;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetModel;
import com.yihu.ehr.adaption.dataset.service.DataSetModel;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 适配管理方案适配管理
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterDataSet")
@Api(protocols = "https", value = "adapterDataSet", description = "适配数据集接口", tags = {"适配数据集"})
public class AdapterDataSetController extends BaseRestController {

    @Autowired
    AdapterDataSetManager adapterDataSetManager;
    @Autowired
    OrgAdapterPlanManager orgAdapterPlanManager;

    @RequestMapping("/adapterDataSet")
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Result searchAdapterDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "strKey", value = "查询条件", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows) {

        Result result = new Result();
        try {
            List<DataSetModel> dataSet = adapterDataSetManager.searchAdapterDataSet(adapterPlanId, strKey, page, rows);
            int totalCount = adapterDataSetManager.searchDataSetInt(adapterPlanId, strKey);
            result.setSuccessFlg(true);
            result = getResult(dataSet, totalCount, page, rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping("/adapterMetaData/dataSet_id")
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Result searchAdapterMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "dataSetId", value = "dataSetId", defaultValue = "")
            @RequestParam(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "strKey", value = "查询条件", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows) {

        Result result = new Result();
        int totalCount;
        try {
            List<AdapterDataSetModel> adapterDataSets = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, strKey, page, rows);
            totalCount = adapterDataSetManager.searchMetaDataInt(adapterPlanId, dataSetId, strKey);
            result = getResult(adapterDataSets, totalCount, page, rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ID获取数据元适配关系明细")
    public Result getAdapterMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        Result result = new Result();
        try {
            AdapterDataSet adapterDataSet = adapterDataSetManager.findOne(id);
            result.setObj(adapterDataSet);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "修改数据元映射关系")
    public boolean updateAdapterMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            AdapterDataSetModel adapterDataSetModel) {
        try {
            AdapterDataSet adapterDataSet = adapterDataSetManager.findOne(adapterDataSetModel.getId());
            adapterDataSet.setAdapterPlanId(adapterDataSetModel.getAdapterPlanId());
            adapterDataSet.setMetaDataId(adapterDataSetModel.getMetaDataId());
            adapterDataSet.setDataSetId(adapterDataSetModel.getDataSetId());
            adapterDataSet.setOrgDataSetSeq(adapterDataSetModel.getOrgDataSetSeq());
            adapterDataSet.setOrgMetaDataSeq(adapterDataSetModel.getOrgMetaDataSeq());
            adapterDataSet.setDataType(adapterDataSetModel.getDataType());
            adapterDataSet.setDescription(adapterDataSetModel.getDescription());
            if (adapterDataSetModel.getId() == null) {
                OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(adapterDataSetModel.getAdapterPlanId());
                adapterDataSetManager.addAdapterDataSet(apiVersion, adapterDataSet, orgAdapterPlan);
            } else {
                adapterDataSetManager.save(adapterDataSet);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元映射关系")
    public boolean delMetaData(
            @RequestParam("id") Long[] id) {
        try {
            adapterDataSetManager.deleteAdapterDataSet(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
