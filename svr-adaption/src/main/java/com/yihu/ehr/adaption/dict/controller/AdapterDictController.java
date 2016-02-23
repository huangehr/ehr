package com.yihu.ehr.adaption.dict.controller;

import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanService;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.dict.service.AdapterDict;
import com.yihu.ehr.adaption.dict.service.AdapterDictModel;
import com.yihu.ehr.adaption.dict.service.AdapterDictService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MAdapterDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/adapter")
@Api(protocols = "https", value = "adapterDict", description = "适配字典管理接口", tags = {"适配字典管理"})
public class AdapterDictController extends ExtendController<MAdapterDict> {

    @Autowired
    AdapterDictService adapterDictService;
    @Autowired
    OrgAdapterPlanService orgAdapterPlanService;

    @RequestMapping(value = "/plan/{planId}/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "字典适配关系分页查询")
    public Collection searchAdapterDict(
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
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(planId);
        if (orgAdapterPlan == null)
            throw errNotFound();
        List ls = adapterDictService.searchAdapterDict(orgAdapterPlan, code, name, sorts, page, size);
        pagedResponse(request, response, (long) adapterDictService.searchDictInt(orgAdapterPlan, code, name), page, size);
        return ls;
    }

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/items", method = RequestMethod.GET)
    @ApiOperation(value = "字典项适配关系分页查询")
    public Collection searchAdapterDictEntry(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dictId") Long dictId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(planId);
        if (orgAdapterPlan == null)
            throw errNotFound();
        List ls = adapterDictService.searchAdapterDictEntry(orgAdapterPlan, dictId, code, name, sorts, page, size);
        pagedResponse(request, response, (long) adapterDictService.searchDictEntryInt(orgAdapterPlan, dictId, code, name), page, size);
        return ls;
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据字典ID获取字典项适配关系明细")
    public MAdapterDict getAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) {

        return getAdapterDict(id);
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST)
    @ApiOperation(value = "保存字典项映射关系")
    public boolean createAdapterDictEntry(
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestParam(value = "adapterDictModel") String dictJsonModel) throws Exception {

        try {
            return save(jsonToObj(dictJsonModel, AdapterDictModel.class), new AdapterDict());
        } catch (IOException e) {
            throw errParm();
        }
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项映射关系")
    public boolean updateAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestParam(value = "adapterDictModel") String dictJsonModel) throws Exception {

        try {
            AdapterDict adapterDict = adapterDictService.retrieve(id);
            if (adapterDict == null)
                throw errNotFound();
            return save(jsonToObj(dictJsonModel, AdapterDictModel.class), adapterDict);
        } catch (IOException e) {
            throw errParm();
        }
    }

    @RequestMapping(value = "/items", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项映射")
    public boolean delDictEntry(
            @RequestParam("ids") String ids) throws Exception {
        if (StringUtils.isEmpty(ids))
            throw errMissId();
        adapterDictService.delete(ids.split(","));
        return true;
    }

    private MAdapterDict getAdapterDict(long id) {
        return convertToModel(adapterDictService.retrieve(id), MAdapterDict.class);
    }

    private boolean save(AdapterDictModel adapterDictModel, AdapterDict adapterDict) {

        adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
        adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
        adapterDict.setDictId(adapterDictModel.getDictId());
        adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
        adapterDict.setOrgDictItemSeq(adapterDictModel.getOrgDictEntrySeq());
        adapterDict.setDescription(adapterDictModel.getDescription());
        if (adapterDictModel.getId() == null) {
            adapterDictService.addAdapterDict(adapterDict);
        } else {
            adapterDictService.save(adapterDict);
        }
        return true;
    }
}
