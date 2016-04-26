package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.resource.model.RsDimension;
import com.yihu.ehr.resource.service.intf.IDimensionService;
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
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/dimensions")
@Api(value = "dimensions", description = "维度服务接口")
public class DimensionController extends BaseRestController {

    @Autowired
    private IDimensionService dimensionService;

    @ApiOperation("创建维度")
    @RequestMapping(method = RequestMethod.POST)
    public RsDimension createDimension(
            @ApiParam(name="dimension",value="维度",defaultValue = "")
            @RequestParam(name="dimension")String dimension) throws Exception
    {
        RsDimension dimens = toEntity(dimension,RsDimension.class);
        dimens.setId(getObjectId(BizObject.Dimensions));
        dimensionService.createDimension(dimens);
        return dimens;
    }

    @ApiOperation("更新维度")
    @RequestMapping(method = RequestMethod.PUT)
    public RsDimension updateDimension(
            @ApiParam(name="dimension",value="维度",defaultValue="")
            @RequestParam(name="dimension")String dimension) throws Exception
    {
        RsDimension  dimens= toEntity(dimension,RsDimension.class);
        dimensionService.updateDimension(dimens);
        return dimens;
    }

    @ApiOperation("维度删除")
    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    public boolean deleteDimension(
            @ApiParam(name="id",value="维度ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        dimensionService.deleteDimension(id);
        return true;
    }

    @ApiOperation("维度查询")
    @RequestMapping(value="",method = RequestMethod.GET)
    public Collection<RsDimension> queryDimensions(
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
            Page<RsDimension> dimensions = dimensionService.getDimensions(sorts,reducePage(page),size);
            pagedResponse(request,response,dimensions.getTotalElements(),page,size);
            return convertToModels(dimensions.getContent(),new ArrayList<>(dimensions.getNumber()),RsDimension.class,fields);
        }
        else
        {
            List<RsDimension> dimensions = dimensionService.search(fields,filters,sorts,page,size);
            pagedResponse(request,response,dimensionService.getCount(filters),page,size);
            return convertToModels(dimensions,new ArrayList<>(dimensions.size()),RsDimension.class,fields);
        }
    }
}
