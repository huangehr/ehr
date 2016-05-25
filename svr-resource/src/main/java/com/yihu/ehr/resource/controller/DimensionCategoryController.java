package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsDimensionCategory;
import com.yihu.ehr.resource.model.RsDimensionCategory;
import com.yihu.ehr.resource.service.intf.IDimensionCategoryService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
@RestController
@Api(value="DimensionCategory",description = "维度分类")
@RequestMapping(value= ApiVersion.Version1_0 + "/dimensionCategories")
public class DimensionCategoryController extends BaseRestController{
    @Autowired
    private IDimensionCategoryService dmcService;

    @ApiOperation("创建维度类别")
    @RequestMapping(method = RequestMethod.POST)
    public MRsDimensionCategory createDimensionCategory(
            @ApiParam(name="dimensionCategory",value="维度类别",defaultValue = "")
            @RequestParam(value="dimensionCategory")String dimensionCategory) throws Exception
    {
        RsDimensionCategory dmc = toEntity(dimensionCategory,RsDimensionCategory.class);
        dmc.setId(getObjectId(BizObject.DimensionsCategories));
        dmcService.saveDimensionCategory(dmc);
        return convertToModel(dmc,MRsDimensionCategory.class);
    }

    @ApiOperation("更新维度类别")
    @RequestMapping(method = RequestMethod.PUT)
    public MRsDimensionCategory updateDimensionCategory(
            @ApiParam(name="dimensionCategory",value="维度类别",defaultValue="")
            @RequestParam(value="dimensionCategory")String dimensionCategory) throws Exception
    {
        RsDimensionCategory  dmc= toEntity(dimensionCategory,RsDimensionCategory.class);
        dmcService.saveDimensionCategory(dmc);
        return convertToModel(dmc,MRsDimensionCategory.class);
    }

    @ApiOperation("维度类别删除")
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    public boolean deleteDimensionCategory(
            @ApiParam(name="id",value="维度类别ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        dmcService.deleteDimensionCategory(id);
        return true;
    }

    @ApiOperation("维度类别查询")
    @RequestMapping(value="",method = RequestMethod.GET)
    public Page<MRsDimensionCategory> queryCategories(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(value="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(value="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        Pageable pageable = new PageRequest(reducePage(page),size);
        long total = 0;
        Collection<MRsDimensionCategory> rsDimCateGoryList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsDimensionCategory> dmcs = dmcService.getDimensionCategories(sorts,reducePage(page),size);
            total = dmcs.getTotalElements();
            rsDimCateGoryList = convertToModels(dmcs.getContent(),new ArrayList<>(dmcs.getNumber()),MRsDimensionCategory.class,fields);
        }
        else
        {
            List<RsDimensionCategory> dmcs = dmcService.search(fields,filters,sorts,page,size);
            total = dmcService.getCount(filters);
            rsDimCateGoryList =  convertToModels(dmcs,new ArrayList<>(dmcs.size()),MRsDimensionCategory.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsDimensionCategory> rsPage = new PageImpl<MRsDimensionCategory>((List<MRsDimensionCategory>)rsDimCateGoryList,pageable,total);

        return rsPage;
    }
}
