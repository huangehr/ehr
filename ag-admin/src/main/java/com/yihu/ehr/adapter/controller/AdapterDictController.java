package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.service.AdapterDictClient;
import com.yihu.ehr.adapter.service.OrgDictClient;
import com.yihu.ehr.adapter.service.OrgDictEntryClient;
import com.yihu.ehr.adapter.service.PlanClient;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.adapter.AdapterDictDetailModel;
import com.yihu.ehr.agModel.adapter.AdapterDictModel;
import com.yihu.ehr.agModel.adapter.AdapterRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.std.service.DictClient;
import com.yihu.ehr.model.adaption.*;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdDictEntry;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter")
@RestController
@Api(protocols = "https", value = "adapter", description = "字典", tags = {"字典"})
public class AdapterDictController extends ExtendController<AdapterDictModel> {

    @Autowired
    AdapterDictClient adapterDictClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrgDictClient orgDictClient;

    @Autowired
    OrgDictEntryClient orgDictEntryClient;

    @Autowired
    DictClient dictClient;

    @Autowired
    PlanClient planClient;

    @RequestMapping(value = "/plan/{plan_id}/dicts", method = RequestMethod.GET)
    public Envelop searchDicts(
            @ApiParam(name = "plan_id", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "plan_id") Long planId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        try {
            ResponseEntity<Collection<MAdapterRelationship>> responseEntity = adapterDictClient.searchAdapterDict(planId, code, name, sorts, size, page);
            List<MAdapterRelationship> mAdapterRelationships = (List<MAdapterRelationship>) responseEntity.getBody();
            List<AdapterRelationshipModel> relationshipModels = (List<AdapterRelationshipModel>) convertToModels(mAdapterRelationships,
                    new ArrayList<AdapterRelationshipModel>(mAdapterRelationships.size()),
                    AdapterRelationshipModel.class, null);

            return getResult(relationshipModels, getTotalCount(responseEntity), page, size);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/plan/dict/entrys/{plan_id}/{dict_id}", method = RequestMethod.GET)
    public Envelop getAdapterDictEntryByDictId(
            @ApiParam(name = "plan_id", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "plan_id") Long planId,
            @ApiParam(name = "dict_id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dict_id") Long dictId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        try {
            ResponseEntity<Collection<MAdapterDictVo>> responseEntity = adapterDictClient.searchAdapterDictEntry(planId, dictId, code, name, sorts, size, page);
            List<MAdapterDictVo> mAdapterDicts = (List<MAdapterDictVo>) responseEntity.getBody();
            List<AdapterDictModel> adapterDictModels = (List<AdapterDictModel>) convertToModels(mAdapterDicts, new ArrayList<AdapterDictModel>(mAdapterDicts.size())
                    , AdapterDictModel.class, null);
            return getResult(adapterDictModels, getTotalCount(responseEntity), page, size);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/dict/entry/{id}", method = RequestMethod.GET)
    public Envelop getAdapterDictEntry(
            @ApiParam(name = "id", value = "适配关系ID")
            @PathVariable(value = "id") long id) {
        try {
            MAdapterDict mAdapterDict = adapterDictClient.getAdapterDictEntry(id);
            AdapterDictModel adapterDictModel = convertAdapterDictModel(mAdapterDict);
            if (adapterDictModel == null) {
                return failed("数据获取失败!");
            }
            return success(adapterDictModel);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/dict/entry", method = RequestMethod.POST)
    @ApiOperation(value = "保存字典项映射关系")
    public Envelop createAdapterDictEntry(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) {

        try {
            AdapterDictModel adapterDictModel = jsonToObj(model);
            ValidateResult validateResult = validate(adapterDictModel);
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            AdapterDictDetailModel detailModel = convertToModel(adapterDictModel,AdapterDictDetailModel.class);
            MAdapterDict mAdapterDict = adapterDictClient.createAdapterDictEntry(toEncodeJson(detailModel));
            if(mAdapterDict==null)
            {
                return failed("保存失败!");
            }
            adapterDictModel = convertAdapterDictModel(mAdapterDict);
            return success(adapterDictModel);
        } catch (ApiException e){
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return failedSystem();
        }
    };


    @RequestMapping(value = "/dict/entry", method = RequestMethod.PUT)
    public Envelop updateAdapterDictEntry(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) {

        try {
            AdapterDictModel adapterDictModel = jsonToObj(model);
            ValidateResult validateResult = validate(adapterDictModel);
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            AdapterDictDetailModel detailModel = convertToModel(adapterDictModel,AdapterDictDetailModel.class);
            MAdapterDict mAdapterDict =adapterDictClient.updateAdapterDictEntry(detailModel.getId(), toEncodeJson(detailModel));
            if(mAdapterDict==null)
            {
                return  failed("保存失败!");
            }
            adapterDictModel = convertAdapterDictModel(mAdapterDict);
            return success(adapterDictModel);
        } catch (ApiException e){
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/dict/entrys", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配关系", notes = "根据适配关系ID删除适配关系，批量删除时ID以逗号隔开")
    public Envelop delAdapterDictEntry(
            @ApiParam(name = "ids", value = "适配关系ID")
            @RequestParam(value = "ids") String ids){

        try {
            ids = trimEnd(ids, ",");
            if (StringUtils.isEmpty(ids)) {
                return failed("请选择需要删除的数据!");
            }
            boolean result = adapterDictClient.delDictEntry(ids);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    /**
     * 标准字典项的下拉
     * @return
     */
    @RequestMapping("/std_dict_entries/combo")
    @ResponseBody
    public Envelop getStdDictEntry(
            @ApiParam(name = "plan_id", value = "方案编号")
            @RequestParam(value = "plan_id") Long planId,
            @ApiParam(name = "dict_id", value = "字典编号")
            @RequestParam(value = "dict_id") Long dictId,
            @ApiParam(name = "mode", value = "编辑模式： modify、new")
            @RequestParam(value = "mode") String mode,
            @ApiParam(name = "search_name", value = "查询字符串")
            @RequestParam(value = "search_name") String searchName,
            @ApiParam(name = "page", value = "当前页")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "大小")
            @RequestParam(value = "size") int size){

        try {
            ResponseEntity<Collection<MAdapterRelationship>> responseEntity = adapterDictClient.searchStdDictEntry(planId, dictId, searchName, mode, "", size, page);
            List<MAdapterRelationship> stdDictEntries = (List<MAdapterRelationship>) responseEntity.getBody();
            return getResult(stdDictEntries, getTotalCount(responseEntity), page, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public AdapterDictModel convertAdapterDictModel(MAdapterDict mAdapterDict)
    {
        AdapterDictModel dictModel = convertToModel(mAdapterDict,AdapterDictModel.class);

        MAdapterPlan mAdapterPlan = planClient.getAdapterPlanById(dictModel.getAdapterPlanId());
        String versionCode = mAdapterPlan.getVersion();

        long dictId = dictModel.getDictId()==null?0:dictModel.getDictId();
        if(dictId!=0)
        {
            MStdDict mStdDict = dictClient.getCdaDictInfo(dictId, versionCode);
            dictModel.setDictCode(mStdDict == null ? "" : mStdDict.getCode());
            dictModel.setDictName(mStdDict==null?"":mStdDict.getName());
        }

        long dictEntry = dictModel.getDictEntryId()==null?0:dictModel.getDictEntryId();
        if(dictEntry!=0)
        {
            MStdDictEntry dictEntry1 = dictClient.getDictEntry(dictEntry,versionCode);
            dictModel.setDictEntryCode(dictEntry1==null?"":dictEntry1.getCode());
            dictModel.setDictEntryName(dictEntry1 == null ? "" : dictEntry1.getValue());
        }

       long orgDictSeq = dictModel.getOrgDictSeq()==null?0:dictModel.getOrgDictSeq();
        if(orgDictSeq!=0)
        {
            MOrgDict mOrgDict = orgDictClient.getOrgDictBySequence(mAdapterPlan.getOrg(),Integer.parseInt(String.valueOf(orgDictSeq)));
            dictModel.setOrgDictCode(mOrgDict==null?"":mOrgDict.getCode());
            dictModel.setOrgDictName(mOrgDict == null ? "" : mOrgDict.getName());
        }

        long orgDictEntrySeq = dictModel.getOrgDictEntrySeq()==null?0:dictModel.getOrgDictEntrySeq();
        if(orgDictEntrySeq!=0)
        {
            MOrgDictItem mOrgDictItem = orgDictEntryClient.getOrgDicEntryBySequence(mAdapterPlan.getOrg(),Integer.parseInt(String.valueOf(orgDictEntrySeq)));
            dictModel.setOrgDictEntryCode(mOrgDictItem==null?"":mOrgDictItem.getCode());
            dictModel.setOrgDictEntryName(mOrgDictItem == null ? "" : mOrgDictItem.getName());
        }
        return dictModel;
    }
}
