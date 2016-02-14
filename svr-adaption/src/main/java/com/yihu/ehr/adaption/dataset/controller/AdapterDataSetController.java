package com.yihu.ehr.adaption.dataset.controller;

import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetManager;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetModel;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 适配管理方案适配管理
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapter")
@Api(protocols = "https", value = "adapterDataSet", description = "适配数据集接口", tags = {"适配数据集"})
public class AdapterDataSetController extends ExtendController<MAdapterDataSet> {

    @Autowired
    AdapterDataSetManager adapterDataSetManager;
    @Autowired
    OrgAdapterPlanManager orgAdapterPlanManager;

    @RequestMapping(value = "/plan/{planId}/datasets/page", method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Envelop searchAdapterDataSet(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "jsonParm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "jsonParm") String jsonParm) {

        try {
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
            if (orgAdapterPlan==null)
                throw errNotFound();
            PageModel pageModel =  jsonToObj(jsonParm, PageModel.class);
            pageModel.setModelClass(AdapterDataSet.class);
            List dataSet = adapterDataSetManager.searchAdapterDataSet(planId, orgAdapterPlan.getVersion(), pageModel);
            int totalCount = adapterDataSetManager.searchDataSetInt(planId, orgAdapterPlan.getVersion(), pageModel);
            return getResult(dataSet, totalCount);
        } catch (IOException e) {
            throw errParm();
        }  catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping("/plan/{planId}/datasets/{dataSetId}/datametas/page")
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Envelop searchAdapterMetaData(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dataSetId", value = "数据集id", defaultValue = "")
            @PathVariable(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "jsonParm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "jsonParm") String jsonParm) {

        try {
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
            if (orgAdapterPlan==null)
                throw errNotFound();
            PageModel pageModel = jsonToObj(jsonParm, PageModel.class);
            List<AdapterDataSetModel> adapterDataSets = adapterDataSetManager.searchAdapterMetaData(orgAdapterPlan, dataSetId,  pageModel);
            int totalCount = adapterDataSetManager.searchMetaDataInt(orgAdapterPlan, dataSetId, pageModel);
            return getResult(adapterDataSets, totalCount);
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/datameta/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ID获取数据元适配关系明细")
    public MAdapterDataSet getAdapterMetaData(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id) {

        try {
            return  getModel(adapterDataSetManager.findOne(id));
        } catch (Exception e) {
            throw  errSystem();
        }
    }


    @RequestMapping(value = "/datameta/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元映射关系")
    public boolean updateAdapterMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "jsonModel", value = "数据元模型", defaultValue = "")
            @RequestParam(value = "jsonModel") String jsonModel) {
        try {
            AdapterDataSet adapterDataSet = adapterDataSetManager.findOne(id);
            if(adapterDataSet==null)
                throw errNotFound();
            return saveAdapterMetaData(adapterDataSet, jsonModel, apiVersion);
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/datameta", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元映射关系")
    public boolean createAdapterMetaData(
            @ApiParam(name = "jsonModel", value = "数据元模型", defaultValue = "")
            @RequestParam(value = "jsonModel") String jsonModel) {
        try {
            return saveAdapterMetaData(new AdapterDataSet(), jsonModel, "");
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/datametas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元映射关系")
    public boolean delMetaData(
            @RequestParam("id") String ids) {
        try {
            if (StringUtils.isEmpty(ids))
                throw errMissId();
            adapterDataSetManager.deleteAdapterDataSet(ids.split(","));
            return true;
        } catch (Exception e) {
            throw errSystem();
        }
    }


    private boolean saveAdapterMetaData(AdapterDataSet adapterDataSet, String jsonModel, String apiVersion) throws IOException {

        AdapterDataSet adapterDataSetModel = jsonToObj(jsonModel, AdapterDataSet.class);

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
    }
}
