package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsDimensionCategory;
import com.yihu.ehr.resource.service.intf.IDimensionCategoryService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public RsDimensionCategory createDimensionCategory(
            @ApiParam(name="dimensionCategory",value="维度类别",defaultValue = "")
            @RequestParam(name="dimensionCategory")String dimensionCategory) throws Exception
    {
        RsDimensionCategory dmc = toEntity(dimensionCategory,RsDimensionCategory.class);
        dmc.setId(getObjectId(BizObject.DimensionsCategories));
        dmcService.createDimensionCategory(dmc);
        return dmc;
    }

    @ApiOperation("更新维度类别")
    @RequestMapping(method = RequestMethod.PUT)
    public RsDimensionCategory updateDimensionCategory(
            @ApiParam(name="dimensionCategory",value="维度类别",defaultValue="")
            @RequestParam(name="dimensionCategory")String dimensionCategory) throws Exception
    {
        RsDimensionCategory  dmc= toEntity(dimensionCategory,RsDimensionCategory.class);
        dmcService.updateDimensionCategory(dmc);
        return dmc;
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
    public Collection<RsDimensionCategory> queryCategories(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(name="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(name="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(name="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(name="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(name="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsDimensionCategory> dmcs = dmcService.getDimensionCategories(sorts,reducePage(page),size);
            pagedResponse(request,response,dmcs.getTotalElements(),page,size);
            return convertToModels(dmcs.getContent(),new ArrayList<>(dmcs.getNumber()),RsDimensionCategory.class,fields);
        }
        else
        {
            List<RsDimensionCategory> dmcs = dmcService.search(fields,filters,sorts,page,size);
            pagedResponse(request,response,dmcService.getCount(filters),page,size);
            return convertToModels(dmcs,new ArrayList<>(dmcs.size()),RsDimensionCategory.class,fields);
        }
    }
}
