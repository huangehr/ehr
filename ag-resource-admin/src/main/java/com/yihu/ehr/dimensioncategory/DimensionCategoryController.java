package com.yihu.ehr.dimensioncategory;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lyr on 2016/4/25.
 */
@RestController
@Api(value="DimensionCategory",description = "维度分类")
@RequestMapping(value= ApiVersion.Version1_0 + "/dimensionCategories")
public class DimensionCategoryController extends BaseController {
//    @Autowired
//    private IDimensionCategoryService dmcService;
//
//    @ApiOperation("创建维度类别")
//    @RequestMapping(method = RequestMethod.POST)
//    public MRsDimensionCategory createDimensionCategory(
//            @ApiParam(name="dimensionCategory",value="维度类别",defaultValue = "")
//            @RequestParam(name="dimensionCategory")String dimensionCategory) throws Exception
//    {
//        RsDimensionCategory dmc = toEntity(dimensionCategory,RsDimensionCategory.class);
//        dmc.setId(getObjectId(BizObject.DimensionsCategories));
//        dmcService.createDimensionCategory(dmc);
//        return convertToModel(dmc,MRsDimensionCategory.class);
//    }
//
//    @ApiOperation("更新维度类别")
//    @RequestMapping(method = RequestMethod.PUT)
//    public MRsDimensionCategory updateDimensionCategory(
//            @ApiParam(name="dimensionCategory",value="维度类别",defaultValue="")
//            @RequestParam(name="dimensionCategory")String dimensionCategory) throws Exception
//    {
//        RsDimensionCategory  dmc= toEntity(dimensionCategory,RsDimensionCategory.class);
//        dmcService.updateDimensionCategory(dmc);
//        return convertToModel(dmc,MRsDimensionCategory.class);
//    }
//
//    @ApiOperation("维度类别删除")
//    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
//    public boolean deleteDimensionCategory(
//            @ApiParam(name="id",value="维度类别ID",defaultValue = "")
//            @PathVariable(value="id") String id) throws Exception
//    {
//        dmcService.deleteDimensionCategory(id);
//        return true;
//    }
//
//    @ApiOperation("维度类别查询")
//    @RequestMapping(value="",method = RequestMethod.GET)
//    public Page<MRsDimensionCategory> queryCategories(
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
//        Collection<MRsDimensionCategory> rsDimCateGoryList;
//
//        //过滤条件为空
//        if(StringUtils.isEmpty(filters))
//        {
//            Page<RsDimensionCategory> dmcs = dmcService.getDimensionCategories(sorts,reducePage(page),size);
//            total = dmcs.getTotalElements();
//            rsDimCateGoryList = convertToModels(dmcs.getContent(),new ArrayList<>(dmcs.getNumber()),MRsDimensionCategory.class,fields);
//        }
//        else
//        {
//            List<RsDimensionCategory> dmcs = dmcService.search(fields,filters,sorts,page,size);
//            total = dmcService.getCount(filters);
//            rsDimCateGoryList =  convertToModels(dmcs,new ArrayList<>(dmcs.size()),MRsDimensionCategory.class,fields);
//        }
//
//        pagedResponse(request,response,total,page,size);
//        Page<MRsDimensionCategory> rsPage = new PageImpl<MRsDimensionCategory>((List<MRsDimensionCategory>)rsDimCateGoryList,pageable,total);
//
//        return rsPage;
//    }
}
