package com.yihu.ehr.adaption.controller;

import com.yihu.ehr.adaption.common.ExtendEndPoint;
import com.yihu.ehr.adaption.model.AdapterDict;
import com.yihu.ehr.adaption.model.OrgAdapterPlan;
import com.yihu.ehr.adaption.service.AdapterDictService;
import com.yihu.ehr.adaption.service.OrgAdapterPlanService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MAdapterDict;
import com.yihu.ehr.model.adaption.MAdapterDictVo;
import com.yihu.ehr.model.adaption.MAdapterRelationship;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@Api(value = "AdapterDictEndPoint", description = "字典", tags = {"适配服务-字典"})
public class AdapterDictEndPoint extends ExtendEndPoint<MAdapterDict> {

    @Autowired
    private AdapterDictService adapterDictService;
    @Autowired
    private OrgAdapterPlanService orgAdapterPlanService;

    @RequestMapping(value = "/plan/{planId}/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "适配字典分页查询")
    public Collection<MAdapterRelationship> searchAdapterDict(
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

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/entrys", method = RequestMethod.GET)
    @ApiOperation(value = "字典项适配关系分页查询")
    public Collection<MAdapterDictVo> searchAdapterDictEntry(
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

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据字典ID获取字典项适配关系明细")
    public MAdapterDict getAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) {

        return getAdapterDict(id);
    }

    @RequestMapping(value = "/entry", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存字典项映射关系")
    public MAdapterDict createAdapterDictEntry(
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestBody String dictJsonModel) throws Exception {

        try {
            return getModel(save(dictJsonModel, new AdapterDict()));
        } catch (IOException e) {
            throw errParm();
        }
    }

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改字典项映射关系")
    public MAdapterDict updateAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestBody String dictJsonModel) throws Exception {

        try {
            AdapterDict adapterDict = adapterDictService.retrieve(id);
            if (adapterDict == null)
                throw errNotFound();
            return getModel(save(dictJsonModel, adapterDict));
        } catch (IOException e) {
            throw errParm();
        }
    }

    @RequestMapping(value = "/entrys", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项映射")
    public boolean delDictEntry(
            @RequestParam("ids") String ids) throws Exception {
        if (StringUtils.isEmpty(ids))
            throw errMissId();
        adapterDictService.delete(strToLongArr(ids));
        return true;
    }

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/std_entrys", method = RequestMethod.GET)
    @ApiOperation(value = "过滤后的标准字典项分页查询")
    public Collection<MAdapterRelationship> searchStdDictEntry(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dictId") Long dictId,
            @ApiParam(name = "seach_name", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "seach_name", required = false) String searchName,
            @ApiParam(name = "mode", value = "编辑模式（new/modify）", defaultValue = "")
            @RequestParam(value = "mode", required = false) String mode,
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
        List ls = adapterDictService.searchStdDictEntry(orgAdapterPlan, dictId, searchName, mode, sorts, page, size);
        pagedResponse(request, response, (long) adapterDictService.countStdDictEntry(orgAdapterPlan, dictId, searchName, mode), page, size);
        return ls;
    }

    private MAdapterDict getAdapterDict(long id) {
        return convertToModel(adapterDictService.retrieve(id), MAdapterDict.class);
    }

    private AdapterDict save(String dictJsonModel, AdapterDict adapterDict) throws IOException {

        MAdapterDict adapterDictModel = toDecodeObj(dictJsonModel, MAdapterDict.class);
        adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
        adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
        adapterDict.setDictId(adapterDictModel.getDictId());
        adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
        adapterDict.setOrgDictEntrySeq(adapterDictModel.getOrgDictEntrySeq());
        adapterDict.setDescription(adapterDictModel.getDescription());
        if (adapterDictModel.getId() == null) {
            return adapterDictService.addAdapterDict(adapterDict);
        } else {
            return adapterDictService.save(adapterDict);
        }
    }
}
