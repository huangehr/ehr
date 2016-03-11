package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.agModel.adapter.AdapterDataVoModel;
import com.yihu.ehr.agModel.adapter.AdapterDataSetModel;
import com.yihu.ehr.agModel.adapter.DataSetModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.ha.adapter.service.AdapterDataSetClient;
import com.yihu.ehr.ha.adapter.utils.ExtendController;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.validate.Valid;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter")
@RestController
public class AdapterDataSetController extends ExtendController<AdapterDataSetModel> {

    @Autowired
    AdapterDataSetClient adapterDataSetClient;

    @RequestMapping(value = "/metadata/{id}", method = RequestMethod.GET)
    public AdapterDataSetModel getAdapterMetaDataById(
            @ApiParam(name = "id", value = "适配关系ID")
            @PathVariable(value = "id") Long id) throws Exception {

        MAdapterDataSet dataSet = adapterDataSetClient.getAdapterMetaData(id);
        return convertToModel(dataSet, AdapterDataSetModel.class);
    }

    @RequestMapping(value = "/plan/{planId}/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Collection<DataSetModel> searchAdapterDataSet(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        return convertToModels(
                adapterDataSetClient.searchAdapterDataSet(planId, code, name, sorts, size, page),
                new ArrayList<>(), DataSetModel.class, "");
    }

    @RequestMapping(value = "/plan/{planId}/datasets/{dataSetId}/datametas",method = RequestMethod.GET)
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Collection<AdapterDataVoModel> searchAdapterMetaData(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dataSetId", value = "数据集id", defaultValue = "")
            @PathVariable(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        return convertToModels(
                adapterDataSetClient.searchAdapterMetaData(planId, dataSetId, code, name, sorts, size, page),
                new ArrayList<>(), AdapterDataVoModel.class, ""
        );
    }


    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    public Envelop updateAdapterMetaData(
            @ApiParam(name = "model", value = "说明")
            @RequestParam(value = "model") String model) {

        try {
            AdapterDataSetModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            return success(adapterDataSetClient.updateAdapterMetaData(dataModel.getId(), model));
        } catch (ApiException e) {
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/metadata", method = RequestMethod.POST)
    public Envelop addAdapterMetaData(
            @ApiParam(name = "model", value = "说明")
            @Valid @RequestParam(value = "model") AdapterDataSetModel model) throws Exception{

//        try {
//            throw new Exception("chucuo");
            return success(
                    adapterDataSetClient.createAdapterMetaData(  objToJson(model) ));
//        } catch (Exception e) {
//            if(e.getCause() instanceof FeignException){
//                return failed((e.getCause()).getMessage());
//            }
//            e.printStackTrace();
//            return failedSystem();
//        }
    }


    @RequestMapping(value = "/metadatas", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配关系", notes = "根据适配关系ID删除适配关系，批量删除时ID以逗号隔开")
    public boolean deleteAdapterMetaData(
            @ApiParam(name = "ids", value = "适配关系ID")
            @RequestParam(value = "ids") String ids) throws Exception{

        return adapterDataSetClient.delMetaData(ids);
    }




    /***********************************  放到第三方  ******************************************/
    @RequestMapping(value = "/getStdMetaData", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准数据元", produces = "application/json", notes = "获取未适配的标准数据元，查询条件(mode)为 modify/view")
    public Object getStdMetaData(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "方案ID")
            @RequestParam(value = "adapterPlanId") long adapterPlanId,
            @ApiParam(name = "dataSetId", value = "标准数据集ID")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "mode", value = "查询条件")
            @RequestParam(value = "mode") String mode) {

        return null;
    }

    @RequestMapping(value = "/getOrgDataSet", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构数据集", produces = "application/json", notes = "获取机构数据集")
    public Object getOrgDataSet(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "adapterPlanId", value = "方案ID")
                                @RequestParam(value = "adapterPlanId") long adapterPlanId) {
        return null;
    }

    @RequestMapping("/getOrgMetaData")
    @ApiOperation(value = "获取未适配的机构数据元", produces = "application/json", notes = "获取未适配的机构数据元")
    public Object getOrgMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "orgDataSetSeq", value = "机构数据集序号")
                                 @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
                                 @ApiParam(name = "adapterPlanId", value = "适配方案ID")
                                 @RequestParam(value = "adapterPlanId") Long adapterPlanId) {
        return null;
    }
}
