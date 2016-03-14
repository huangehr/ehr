package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.agModel.adapter.AdapterDataSetModel;
import com.yihu.ehr.agModel.adapter.AdapterRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.ha.adapter.service.AdapterDataSetClient;
import com.yihu.ehr.ha.adapter.utils.ExtendController;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.model.adaption.MAdapterRelationship;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.validate.Valid;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public Envelop getAdapterMetaDataById(
            @ApiParam(name = "id", value = "适配关系ID")
            @PathVariable(value = "id") Long id) {
        try {
            MAdapterDataSet dataSet = adapterDataSetClient.getAdapterMetaData(id);
            if (dataSet == null) {
                return failed("数据获取失败!");
            }
            return success(convertToModel(dataSet, AdapterDataSetModel.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/plan/{planId}/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Envelop searchAdapterDataSet(
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

        ResponseEntity<Collection<MAdapterRelationship>> responseEntity = adapterDataSetClient.searchAdapterDataSet(planId, code, name, sorts, size, page);
        List<MAdapterRelationship> mAdapterRelationships = (List<MAdapterRelationship>) responseEntity.getBody();
        List<AdapterRelationshipModel> adapterRelationshipModels = (List<AdapterRelationshipModel>) convertToModels(
                mAdapterRelationships,
                new ArrayList<AdapterRelationshipModel>(mAdapterRelationships.size()),
                AdapterRelationshipModel.class, "");

        return getResult(adapterRelationshipModels, getTotalCount(responseEntity), page, size);
    }

    @RequestMapping(value = "/plan/{planId}/datasets/{dataSetId}/datametas", method = RequestMethod.GET)
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Envelop searchAdapterMetaData(
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

        ResponseEntity<Collection<MAdapterDataSet>> responseEntity = adapterDataSetClient.searchAdapterMetaData(planId, dataSetId, code, name, sorts, size, page);
        List<MAdapterDataSet> mAdapterDataSets = (List<MAdapterDataSet>) responseEntity.getBody();
        List<AdapterDataSetModel> adapterDataSetModels = (List<AdapterDataSetModel>) convertToModels(
                mAdapterDataSets,
                new ArrayList<>(), AdapterDataSetModel.class, "");

        return getResult(adapterDataSetModels, getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    public Envelop updateAdapterMetaData(
            @ApiParam(name = "model", value = "说明")
            @RequestParam(value = "model") String model) {

        try {
            AdapterDataSetModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if (!validateResult.isRs()) {
                return failed(validateResult.getMsg());
            }
            MAdapterDataSet mAdapterDataSet = adapterDataSetClient.updateAdapterMetaData(dataModel.getId(), model);
            if (mAdapterDataSet == null) {
                return failed("保存失败!");
            }
            return success(getModel(mAdapterDataSet));
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
            @Valid @RequestParam(value = "model") String model) throws Exception {

        try {
            AdapterDataSetModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if (!validateResult.isRs()) {
                return failed(validateResult.getMsg());
            }
            MAdapterDataSet mAdapterDataSet = adapterDataSetClient.createAdapterMetaData(model);
            if (mAdapterDataSet == null) {
                return failed("保存失败!");
            }
            return success(getModel(mAdapterDataSet));
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/metadatas", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配关系", notes = "根据适配关系ID删除适配关系，批量删除时ID以逗号隔开")
    public Envelop deleteAdapterMetaData(
            @ApiParam(name = "ids", value = "适配关系ID")
            @RequestParam(value = "ids") String ids) {

        try {
            ids = trimEnd(ids, ",");
            if (StringUtils.isEmpty(ids)) {
                return failed("请选择需要删除的数据!");
            }
            boolean result = adapterDataSetClient.delMetaData(ids);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
