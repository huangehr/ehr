package com.yihu.ehr.adaption.dataset.controller;

import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanService;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
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
 * 适配管理方案适配管理
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/adapter")
@Api(protocols = "https", value = "adapterDataSet", description = "适配数据集接口", tags = {"适配数据集"})
public class AdapterDataSetController extends ExtendController<MAdapterDataSet> {

    @Autowired
    AdapterDataSetService adapterDataSetService;
    @Autowired
    OrgAdapterPlanService orgAdapterPlanService;

    @RequestMapping(value = "/plan/{planId}/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    public Collection searchAdapterDataSet(
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
            HttpServletResponse response) throws Exception{

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(planId);
        if (orgAdapterPlan==null)
            throw errNotFound();
        List ls = adapterDataSetService.searchAdapterDataSet(orgAdapterPlan, code, name, sorts, page, size);
        pagedResponse(request, response, adapterDataSetService.searchDataSetInt(orgAdapterPlan, code, name), page, size);
        return ls;
    }

    @RequestMapping("/plan/{planId}/datasets/{dataSetId}/datametas")
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    public Collection searchAdapterMetaData(
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
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(planId);
        if (orgAdapterPlan==null)
            throw errNotFound();
        List ls = adapterDataSetService.searchAdapterMetaData(orgAdapterPlan, dataSetId, code, name, sorts, page, size);
        pagedResponse(request, response, adapterDataSetService.searchMetaDataInt(orgAdapterPlan, dataSetId, code, name), page, size);
        return ls;
    }


    @RequestMapping(value = "/datameta/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ID获取数据元适配关系明细")
    public MAdapterDataSet getAdapterMetaData(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id) throws Exception{

        return  getModel(adapterDataSetService.retrieve(id));
    }


    @RequestMapping(value = "/datameta/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元映射关系")
    public boolean updateAdapterMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "jsonModel", value = "数据元模型", defaultValue = "")
            @RequestParam(value = "jsonModel") String jsonModel) throws Exception{

        AdapterDataSet adapterDataSet = adapterDataSetService.retrieve(id);
        if(adapterDataSet==null)
            throw errNotFound();
        return saveAdapterMetaData(adapterDataSet, jsonModel);
    }


    @RequestMapping(value = "/datameta", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元映射关系")
    public boolean createAdapterMetaData(
            @ApiParam(name = "jsonModel", value = "数据元模型", defaultValue = "")
            @RequestParam(value = "jsonModel") String jsonModel) throws Exception{

        return saveAdapterMetaData(new AdapterDataSet(), jsonModel);
    }

    @RequestMapping(value = "/datametas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元映射关系")
    public boolean delMetaData(
            @RequestParam("ids") String ids) throws Exception{

        if (StringUtils.isEmpty(ids))
            throw errMissId();
        adapterDataSetService.deleteAdapterDataSet(ids.split(","));
        return true;
    }


    private boolean saveAdapterMetaData(AdapterDataSet adapterDataSet, String jsonModel)  {
        String apiVersion = ApiVersionPrefix.Version1_0;
        AdapterDataSet adapterDataSetModel = null;
        try {
            adapterDataSetModel = jsonToObj(jsonModel, AdapterDataSet.class);
        } catch (IOException e) {
            throw errParm();
        }

        adapterDataSet.setAdapterPlanId(adapterDataSetModel.getAdapterPlanId());
        adapterDataSet.setMetaDataId(adapterDataSetModel.getMetaDataId());
        adapterDataSet.setDataSetId(adapterDataSetModel.getDataSetId());
        adapterDataSet.setOrgDataSetSeq(adapterDataSetModel.getOrgDataSetSeq());
        adapterDataSet.setOrgMetaDataSeq(adapterDataSetModel.getOrgMetaDataSeq());
        adapterDataSet.setDataType(adapterDataSetModel.getDataType());
        adapterDataSet.setDescription(adapterDataSetModel.getDescription());
        if (adapterDataSetModel.getId() == null) {
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(adapterDataSetModel.getAdapterPlanId());
            adapterDataSetService.addAdapterDataSet(apiVersion, adapterDataSet, orgAdapterPlan);
        } else {
            adapterDataSetService.save(adapterDataSet);
        }
        return true;
    }
}
