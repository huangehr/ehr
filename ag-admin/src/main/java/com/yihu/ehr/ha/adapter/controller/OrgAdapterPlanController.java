package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.ha.adapter.service.PlanClient;
import com.yihu.ehr.ha.adapter.utils.ExtendController;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrgAdapterPlanController extends ExtendController<AdapterPlanModel> {

    @Autowired
    PlanClient planClient;


    @RequestMapping(value = "/plan", method = RequestMethod.POST)
    @ApiOperation(value = "新增适配方案信息")
    public Envelop addAdapterPlan(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model,
            @ApiParam(name = "isCover", value = "是否覆盖")
            @RequestParam(value = "isCover") String isCover)  {

        try {
            AdapterPlanModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            return success(planClient.saveAdapterPlan(objToJson(dataModel), isCover));
        } catch (ApiException e) {
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e){
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
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            return success(planClient.updateAdapterPlan(dataModel.getId(), objToJson(dataModel)));
        } catch (ApiException e) {
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    public Collection<AdapterPlanModel> searchAdapterPlan(
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
            return convertToModels(
                    planClient.searchAdapterPlan(fields, filters, sorts, size, page ),
                    new ArrayList<>(), AdapterPlanModel.class, "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    public AdapterPlanModel getAdapterPlanById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) throws Exception {

        return getModel(planClient.getAdapterPlanById(id));
    }


    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案", notes = "删除适配方案信息，批量删除时，Id以逗号隔开")
    public boolean delAdapterPlan(
            @ApiParam(name = "ids", value = "方案ID")
            @RequestParam("ids") String ids) throws Exception {

        return planClient.delAdapterPlan(ids);
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


    @RequestMapping(value = "/plan/{planId}/adapterCustomizes", method = RequestMethod.GET)
    @ApiOperation(value = "获取定制信息")
    public Map getAdapterCustomize(
            @ApiParam(name = "planId", value = "方案ID")
            @RequestParam(value = "planId") long planId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam("version") String version) throws Exception {

        return planClient.getAdapterCustomize(planId, version);
    }


    @RequestMapping(value = "/plan/{planId}/adapterDataSet", method = RequestMethod.GET)
    @ApiOperation(value = "定制数据集")
    public boolean adapterDataSet(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "customizeData", value = "customizeData", defaultValue = "")
            @RequestParam("customizeData") String customizeData) throws Exception{

        return planClient.adapterDataSet(planId, customizeData);
    }



    /********************************  放到第三方机构网关做  ******************************/
    @RequestMapping(value = "/orgIsExistData", method = RequestMethod.GET)
    public String orgIsExistData(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "orgCode", value = "机构代码")
                                 @RequestParam(value = "orgCode") String orgCode) {
        return null;
    }

    @RequestMapping(value = "/getAdapterOrgList", method = RequestMethod.GET)
    public String getAdapterOrgList(
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type) {
        return null;
    }
}
