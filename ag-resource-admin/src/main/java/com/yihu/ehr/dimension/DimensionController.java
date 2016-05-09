package com.yihu.ehr.dimension;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/dimensions")
@Api(value = "dimensions", description = "维度服务接口")
public class DimensionController extends BaseController {


//    @ApiOperation("创建维度")
//    @RequestMapping(method = RequestMethod.POST)
//    public String createDimension(
//            @ApiParam(name="dimension",value="维度",defaultValue = "")
//            @RequestParam(name="dimension")String dimension) throws Exception {
//
//        return null;
//    }


//
//    @ApiOperation("更新维度")
//    @RequestMapping(method = RequestMethod.PUT)
//    public MRsDimension updateDimension(
//            @ApiParam(name="dimension",value="维度",defaultValue="")
//            @RequestParam(name="dimension")String dimension) throws Exception
//    {
//        RsDimension  dimens= toEntity(dimension,RsDimension.class);
//        dimensionService.updateDimension(dimens);
//        return convertToModel(dimens,MRsDimension.class);
//    }
//
//    @ApiOperation("维度删除")
//    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
//    public boolean deleteDimension(
//            @ApiParam(name="id",value="维度ID",defaultValue = "")
//            @PathVariable(value="id") String id) throws Exception
//    {
//        dimensionService.deleteDimension(id);
//        return true;
//    }
//
//    @ApiOperation("维度查询")
//    @RequestMapping(value="",method = RequestMethod.GET)
//    public Page<MRsDimension> queryDimensions(
//            @ApiParam(name="fields",value="返回字段",defaultValue = "")
//            @RequestParam(name="fields",required = false)String fields,
//            @ApiParam(name="filters",value="过滤",defaultValue = "")
//            @RequestParam(name="filters",required = false)String filters,
//            @ApiParam(name="sorts",value="排序",defaultValue = "")
//            @RequestParam(name="sorts",required = false)String sorts,
//            @ApiParam(name="page",value="页码",defaultValue = "1")
//            @RequestParam(name="page",required = false)int page,
//            @ApiParam(name="size",value="分页大小",defaultValue = "15")
//            @RequestParam(name="size",required = false)int size,
//            HttpServletRequest request,
//            HttpServletResponse response) throws Exception
//    {
//        Pageable pageable = new PageRequest(reducePage(page),size);
//        long total = 0;
//        Collection<MRsDimension> rsDimensionList;
//
//        //过滤条件为空
//        if(StringUtils.isEmpty(filters))
//        {
//            Page<RsDimension> dimensions = dimensionService.getDimensions(sorts,reducePage(page),size);
//            total = dimensions.getTotalElements();
//            rsDimensionList =  convertToModels(dimensions.getContent(),new ArrayList<>(dimensions.getNumber()),MRsDimension.class,fields);
//        }
//        else
//        {
//            List<RsDimension> dimensions = dimensionService.search(fields,filters,sorts,page,size);
//            total =  dimensionService.getCount(filters);
//            rsDimensionList =  convertToModels(dimensions,new ArrayList<>(dimensions.size()),MRsDimension.class,fields);
//        }
//
//        pagedResponse(request,response,total,page,size);
//        Page<MRsDimension> rsPage = new PageImpl<MRsDimension>((List<MRsDimension>)rsDimensionList,pageable,total);
//
//        return rsPage;
//    }
}
