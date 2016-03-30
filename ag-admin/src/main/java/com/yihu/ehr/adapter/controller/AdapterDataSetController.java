package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.adapter.service.AdapterDataSetClient;
import com.yihu.ehr.adapter.service.OrgMetaDataClient;
import com.yihu.ehr.adapter.service.PlanClient;
import com.yihu.ehr.agModel.adapter.AdapterDataSetDetailModel;
import com.yihu.ehr.agModel.adapter.AdapterDataSetModel;
import com.yihu.ehr.agModel.adapter.AdapterRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.adapter.service.OrgDataSetClient;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.std.service.DataSetClient;
import com.yihu.ehr.model.adaption.*;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
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

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter")
@RestController
@Api(protocols = "https", value = "adapter", description = "数据集", tags = {"数据集"})
public class AdapterDataSetController extends ExtendController<AdapterDataSetModel> {

    @Autowired
    AdapterDataSetClient adapterDataSetClient;

    @Autowired
    DataSetClient dataSetClient;

    @Autowired
    OrgDataSetClient orgDataSetClient;

    @Autowired
    OrgMetaDataClient orgMetaDataClient;

    @Autowired
    PlanClient planClient;

    @RequestMapping(value = "/metadata/{id}", method = RequestMethod.GET)
    public Envelop getAdapterMetaDataById(
            @ApiParam(name = "id", value = "适配关系ID")
            @PathVariable(value = "id") Long id) {
        try {
            MAdapterDataSet dataSet = adapterDataSetClient.getAdapterMetaData(id);
            if (dataSet == null) {
                return failed("数据获取失败!");
            }
            return success(convertAdapterDataSetModel(dataSet));
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/plan/data_set/{plan_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Envelop searchAdapterDataSet(
            @ApiParam(name = "plan_id", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "plan_id") long planId,
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
                AdapterRelationshipModel.class, null);

        return getResult(adapterRelationshipModels, getTotalCount(responseEntity), page, size);
    }

    @RequestMapping(value = "/plan/meta_data/{plan_id}/{set_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Envelop searchAdapterMetaData(
            @ApiParam(name = "plan_id", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "plan_id") Long planId,
            @ApiParam(name = "set_id", value = "数据集id", defaultValue = "")
            @PathVariable(value = "set_id") Long dataSetId,
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

        ResponseEntity<Collection<MAdapterDataVo>> responseEntity = adapterDataSetClient.searchAdapterMetaData(planId, dataSetId, code, name, sorts, size, page);
        List<MAdapterDataVo> mAdapterDataSets = (List<MAdapterDataVo>) responseEntity.getBody();
        List<AdapterDataSetModel> adapterDataSetModels = (List<AdapterDataSetModel>) convertToModels(
                mAdapterDataSets,
                new ArrayList<>(), AdapterDataSetModel.class, null);

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

            AdapterDataSetDetailModel detailModel = convertToModel(dataModel,AdapterDataSetDetailModel.class);

            MAdapterDataSet mAdapterDataSet = adapterDataSetClient.
                    updateAdapterMetaData(detailModel.getId(), toEncodeJson(detailModel));
            if (mAdapterDataSet == null) {
                return failed("保存失败!");
            }
            AdapterDataSetModel adapterDataSetModel = convertAdapterDataSetModel(mAdapterDataSet);
            return success(adapterDataSetModel);
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
            @RequestParam(value = "model") String model) throws Exception {

        try {
            AdapterDataSetModel dataModel = jsonToObj(model);
            ValidateResult validateResult = validate(dataModel);
            if (!validateResult.isRs()) {
                return failed(validateResult.getMsg());
            }

            AdapterDataSetDetailModel detailModel = convertToModel(dataModel,AdapterDataSetDetailModel.class);
            MAdapterDataSet mAdapterDataSet = adapterDataSetClient.createAdapterMetaData(toEncodeJson(detailModel));
            if (mAdapterDataSet == null) {
                return failed("保存失败!");
            }
            AdapterDataSetModel adapterDataSetModel = convertAdapterDataSetModel(mAdapterDataSet);
            return success(adapterDataSetModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/meta_data", method = RequestMethod.DELETE)
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

    /**
     * 标准字典项的下拉
     * @return
     */
    @RequestMapping("/std_meta_data/combo")
    @ResponseBody
    public Envelop getStdDictEntry(
            @ApiParam(name = "plan_id", value = "方案编号")
            @RequestParam(value = "plan_id") Long planId,
            @ApiParam(name = "data_set_id", value = "字典编号")
            @RequestParam(value = "data_set_id") Long dataSetId,
            @ApiParam(name = "mode", value = "编辑模式： modify、new")
            @RequestParam(value = "mode") String mode,
            @ApiParam(name = "search_name", value = "查询字符串")
            @RequestParam(value = "search_name") String searchName,
            @ApiParam(name = "page", value = "当前页")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "大小")
            @RequestParam(value = "size") int size){

        try {
            ResponseEntity<Collection<MAdapterRelationship>> responseEntity = adapterDataSetClient.searchStdMeta(planId, dataSetId, searchName, mode, "", size, page);
            List<MAdapterRelationship> stdMeta = (List<MAdapterRelationship>) responseEntity.getBody();
            return getResult(stdMeta, getTotalCount(responseEntity), page, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public AdapterDataSetModel convertAdapterDataSetModel(MAdapterDataSet mAdapterDataSet){

        AdapterDataSetModel adapterDataSetModel = convertToModel(mAdapterDataSet,AdapterDataSetModel.class);

        long planId = adapterDataSetModel.getAdapterPlanId();
        MAdapterPlan mAdapterPlan = planClient.getAdapterPlanById(planId);
        String versionCode = mAdapterPlan.getVersion();

        long dataSetId = adapterDataSetModel.getDataSetId()==null?0:adapterDataSetModel.getDataSetId();
        if (dataSetId != 0) {
            MStdDataSet mStdDataSet = dataSetClient.getDataSet(dataSetId, versionCode);
            adapterDataSetModel.setDataSetCode(mStdDataSet == null ? "" : mStdDataSet.getCode());
            adapterDataSetModel.setDataSetName(mStdDataSet == null ? "" : mStdDataSet.getName());
        }

        long metaDataId = adapterDataSetModel.getMetaDataId()==null?0:adapterDataSetModel.getMetaDataId();
        if (metaDataId != 0) {
            MStdMetaData mStdMetaData = dataSetClient.getMetaData(metaDataId, versionCode);
            adapterDataSetModel.setMetaDataCode(mStdMetaData == null ? "" : mStdMetaData.getCode());
            adapterDataSetModel.setMetaDataName(mStdMetaData == null ? "" : mStdMetaData.getName());
        }

        long orgDataSetSeq = adapterDataSetModel.getOrgDataSetSeq()==null?0:adapterDataSetModel.getOrgDataSetSeq();
        if (orgDataSetSeq != 0) {
            MOrgDataSet mOrgDataSet = orgDataSetClient.getDataSetBySequence(mAdapterPlan.getOrg(), orgDataSetSeq);
            adapterDataSetModel.setOrgDataSetCode(mOrgDataSet == null ? "" : mOrgDataSet.getCode());
            adapterDataSetModel.setOrgDataSetName(mOrgDataSet == null ? "" : mOrgDataSet.getName());
        }

        long orgMateDataSeq =  adapterDataSetModel.getOrgMetaDataSeq()==null?0:adapterDataSetModel.getOrgMetaDataSeq();
        if(orgMateDataSeq!=0)
        {
            MOrgMetaData mOrgMetaData = orgMetaDataClient.getMetaDataBySequence(mAdapterPlan.getOrg(),Integer.parseInt(String.valueOf(orgMateDataSeq)));
            adapterDataSetModel.setOrgMetaDataCode(mOrgMetaData==null?"":mOrgMetaData.getCode());
            adapterDataSetModel.setOrgMetaDataName(mOrgMetaData==null?"":mOrgMetaData.getName());
        }
        return adapterDataSetModel;
    }
}
