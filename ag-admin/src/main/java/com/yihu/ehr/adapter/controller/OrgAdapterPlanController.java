package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.adapter.service.AdapterOrgClient;
import com.yihu.ehr.adapter.service.PlanClient;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.adapter.AdapterPlanDetailModel;
import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.std.service.CDAVersionClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter")
@RestController
@Api(protocols = "https", value = "adapter", description = "适配方案", tags = {"适配方案"})
public class OrgAdapterPlanController extends ExtendController<AdapterPlanModel> {

    @Autowired
    PlanClient planClient;

    @Autowired
    ConventionalDictEntryClient dictEntryClient;

    @Autowired
    AdapterOrgClient adapterOrgClient;

    @Autowired
    CDAVersionClient cdaVersionClient;

    @RequestMapping(value = "/plan", method = RequestMethod.POST)
    @ApiOperation(value = "新增适配方案信息")
    public Envelop addAdapterPlan(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model,
            @ApiParam(name = "isCover", value = "是否覆盖")
            @RequestParam(value = "isCover") String isCover) {

        try {
            AdapterPlanModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if (!validateResult.isRs()) {
                return failed(validateResult.getMsg());
            }

            AdapterPlanDetailModel detailModel = convertToModel(dataModel, AdapterPlanDetailModel.class);

            MAdapterPlan mAdapterPlan = planClient.saveAdapterPlan(toEncodeJson(detailModel), isCover);
            if (mAdapterPlan == null) {
                return failed("保存失败!");
            }
            AdapterPlanModel adapterPlanModel = ConvertAdapterPlanModel(convertToModel(mAdapterPlan, AdapterPlanModel.class));
            return success(adapterPlanModel);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/plan", method = RequestMethod.PUT)
    @ApiOperation(value = "修改适配方案信息")
    public Envelop updateAdapterPlan(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) {

        try {
            AdapterPlanModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if (!validateResult.isRs()) {
                return failed(validateResult.getMsg());
            }
            AdapterPlanDetailModel detailModel = convertToModel(dataModel, AdapterPlanDetailModel.class);
            MAdapterPlan mAdapterPlan = planClient.updateAdapterPlan(detailModel.getId(), toEncodeJson(detailModel));
            if (mAdapterPlan == null) {
                return failed("保存失败!");
            }
            AdapterPlanModel adapterPlanModel = ConvertAdapterPlanModel(convertToModel(mAdapterPlan, AdapterPlanModel.class));
            return success(adapterPlanModel);
        } catch (ApiException e) {
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    public Envelop searchAdapterPlan(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        try {
            ResponseEntity<Collection<MAdapterPlan>> responseEntity = planClient.searchAdapterPlan(fields, filters, sorts, size, page);
            List<MAdapterPlan> mAdapterPlans = (List<MAdapterPlan>) responseEntity.getBody();
            List<AdapterPlanModel> adapterPlanModels = (List<AdapterPlanModel>) convertToModels(
                    mAdapterPlans,
                    new ArrayList<AdapterPlanModel>(mAdapterPlans.size()), AdapterPlanModel.class, null);
            if (adapterPlanModels != null) {
                for (int i = 0; i < adapterPlanModels.size(); i++) {
                    ConvertAdapterPlanModel(adapterPlanModels.get(i));
                }
            }
            return getResult(adapterPlanModels, getTotalCount(responseEntity), page, size);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    public Envelop getAdapterPlanById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) {
        try {
            AdapterPlanModel adapterPlanModel = getModel(planClient.getAdapterPlanById(id));
            if (adapterPlanModel == null) {
                return failed("数据获取失败!");
            }
            adapterPlanModel = ConvertAdapterPlanModel(adapterPlanModel);
            return success(adapterPlanModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案", notes = "删除适配方案信息，批量删除时，Id以逗号隔开")
    public Envelop delAdapterPlan(
            @ApiParam(name = "ids", value = "方案ID")
            @RequestParam("ids") String ids) throws Exception {
        ids = trimEnd(ids, ",");
        if (StringUtils.isEmpty(ids)) {
            return failed("请选择需要删除的方案!");
        }
        boolean result = planClient.delAdapterPlan(ids);
        if (!result) {
            return failed("删除失败!");
        }

        return success(null);
    }


    @RequestMapping(value = "/plans/list", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案信息下拉")
    public List<Map<String, String>> getAdapterPlanList(
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "versionCode", value = "版本号")
            @RequestParam(value = "versionCode") String versionCode) throws Exception {

        return planClient.getAdapterPlanList(type, versionCode);
    }


    @RequestMapping(value = "/plan/adapterCustomizes/{plan_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取定制信息")
    public Map getAdapterCustomize(
            @ApiParam(name = "plan_id", value = "方案ID")
            @PathVariable(value = "plan_id") long planId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam("version") String version) throws Exception {
        Map map = planClient.getAdapterCustomize(planId, version);
        return map;
    }


//    @RequestMapping(value = "/plan/adapterDataSet/{plan_id}", method = RequestMethod.POST)
//    @ApiOperation(value = "定制数据集")
//    public boolean adapterDataSet(
//            @ApiParam(name = "plan_id", value = "编号", defaultValue = "")
//            @PathVariable("plan_id") Long planId,
//            @ApiParam(name = "customizeData", value = "customizeData", allowMultiple = true, defaultValue = "")
//            @RequestParam("customizeData") String customizeData) throws Exception {
//
//        return planClient.adapterDataSet(planId, customizeData);
//    }

    @RequestMapping(value = "/plan/{plan_id}/public", method = RequestMethod.PUT)
    @ApiOperation(value = "发布方案")
    public boolean adapterDispatch(
            @ApiParam(name = "plan_id", value = "方案ID")
            @PathVariable(value = "plan_id") long planId) throws Exception {

        boolean b = planClient.adapterDispatch(planId);
        return b;
    }

    public AdapterPlanModel ConvertAdapterPlanModel(AdapterPlanModel adapterPlanModel)
    {
        if (adapterPlanModel == null) {
            return null;
        }

        Long parentId = adapterPlanModel.getParentId();
        if(parentId!=null && parentId!=0) {
            AdapterPlanModel parent = getModel(planClient.getAdapterPlanById(parentId));
            if(parent!=null)
                adapterPlanModel.setParentName(parent.getName());
        }

        String type = adapterPlanModel.getType();
        if(StringUtils.isNotEmpty(type))
        {
            MConventionalDict dict =dictEntryClient.getAdapterType(type);
            if(dict!=null)
            {
                adapterPlanModel.setTypeValue(dict.getValue());
            }
        }

        String orgCode = adapterPlanModel.getOrg();
        if(StringUtils.isNotEmpty(orgCode))
        {
            MAdapterOrg org = adapterOrgClient.getAdapterOrg(orgCode);
            if(org!=null)
            {
                adapterPlanModel.setOrgValue(org.getName());
            }
        }

        String versionId = adapterPlanModel.getVersion();
        if(StringUtils.isNotEmpty(versionId))
        {
            MCDAVersion version = cdaVersionClient.getVersion(versionId);
            if(version!=null)
            {
                adapterPlanModel.setVersionName(version.getVersionName());
            }
        }
        return adapterPlanModel;
    }

}
