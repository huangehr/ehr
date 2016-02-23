package com.yihu.ehr.adaption.dict.controller;

import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.dict.service.AdapterDict;
import com.yihu.ehr.adaption.dict.service.AdapterDictManager;
import com.yihu.ehr.adaption.dict.service.AdapterDictModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MAdapterDict;
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
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.CommonVersion + "/adapter")
@Api(protocols = "https", value = "adapterDict", description = "适配字典管理接口", tags = {"适配字典管理"})
public class AdapterDictController extends ExtendController<MAdapterDict> {

    @Autowired
    AdapterDictManager adapterDictManager;
    @Autowired
    OrgAdapterPlanManager orgAdapterPlanManager;
    @RequestMapping(value = "/plan/{planId}/dicts/page", method = RequestMethod.GET)
    @ApiOperation(value = "字典适配关系分页查询")
    public Envelop searchAdapterDict(
            @ApiParam(name = "planId", value = "方案编号", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "jsonParm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "jsonParm") String jsonParm){

        try {
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
            if(orgAdapterPlan==null)
                throw errNotFound();
            PageModel pageModel = jsonToObj(jsonParm, PageModel.class);
            pageModel.setModelClass(AdapterDict.class);
            List<AdapterDictModel> dict = adapterDictManager.searchAdapterDict(orgAdapterPlan, pageModel);
            int totalCount = adapterDictManager.searchDictInt(orgAdapterPlan, pageModel);
            return getResult(dict,totalCount);
        } catch (IOException e) {
            throw errParm();
        }catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/items/page", method = RequestMethod.GET)
    @ApiOperation(value = "字典项适配关系分页查询")
    public Envelop searchAdapterDictEntry(
            @ApiParam(name = "planId", value = "方案编号", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dictId") Long dictId,
            @ApiParam(name = "jsonParm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "jsonParm") String jsonParm){

        try {
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
            if(orgAdapterPlan==null)
                throw errNotFound();
            PageModel pageModel = jsonToObj(jsonParm, PageModel.class);
            pageModel.setModelClass(AdapterDict.class);
            List<AdapterDictModel> adapterDictModels = adapterDictManager.searchAdapterDictEntry(orgAdapterPlan, dictId, pageModel);
            int totalCount = adapterDictManager.searchDictEntryInt(orgAdapterPlan, dictId, pageModel);
            return getResult(adapterDictModels,totalCount);
        } catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据字典ID获取字典项适配关系明细")
    public MAdapterDict getAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id){

        try {
            return getAdapterDict(id);
        } catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST)
    @ApiOperation(value = "保存字典项映射关系")
    public boolean createAdapterDictEntry(
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestParam(value = "adapterDictModel") String dictJsonModel){

        try {
            return save(jsonToObj(dictJsonModel, AdapterDictModel.class), new AdapterDict());
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项映射关系")
    public boolean updateAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestParam(value = "adapterDictModel") String dictJsonModel){

        try {
            AdapterDict adapterDict = adapterDictManager.findOne(id);
            if(adapterDict==null)
                throw errNotFound();
            return save(jsonToObj(dictJsonModel, AdapterDictModel.class), adapterDict);
        } catch (Exception e) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/items", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项映射")
    public boolean delDictEntry(
            @RequestParam("ids") String ids){
        try {
            if (StringUtils.isEmpty(ids))
                throw errMissId();
            adapterDictManager.delete(ids.split(","), "id");
            return true;
        }catch (Exception e){
            throw errSystem();
        }
    }

    private MAdapterDict getAdapterDict(long id){
        return convertToModel(adapterDictManager.findOne(id), MAdapterDict.class);
    }

    private boolean save(AdapterDictModel adapterDictModel, AdapterDict adapterDict){

        adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
        adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
        adapterDict.setDictId(adapterDictModel.getDictId());
        adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
        adapterDict.setOrgDictItemSeq(adapterDictModel.getOrgDictEntrySeq());
        adapterDict.setDescription(adapterDictModel.getDescription());
        if (adapterDictModel.getId()==null){
            adapterDictManager.addAdapterDict(adapterDict);
        }else {
            adapterDictManager.save(adapterDict);
        }
        return true;
    }
}
