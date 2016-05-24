//package com.yihu.ehr.resource.controller;
//
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.model.resource.MRsDimensionCategory;
//import com.yihu.ehr.resource.client.DimensionCategoryClient;
//import com.yihu.ehr.util.controller.BaseRestController;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.web.bind.annotation.*;
//
///**
// * @author linaz
// * @created 2016.05.23 17:11
// */
//@RestController
//@Api(value = "DimensionCategory", description = "维度分类")
//@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
//public class DimensionCategoryController extends BaseController {
//    @Autowired
//    private DimensionCategoryClient dimensionCategoryClient;
//
//    @ApiOperation("创建维度类别")
//    @RequestMapping(value = "/dimensionCategories" , method = RequestMethod.POST)
//    public MRsDimensionCategory createDimensionCategory(
//            @ApiParam(name = "dimensionCategory", value = "维度类别", defaultValue = "")
//            @RequestParam(name = "dimensionCategory") String dimensionCategory) throws Exception {
//        return dimensionCategoryClient.createDimensionCategory(dimensionCategory);
//    }
//
//    @ApiOperation("更新维度类别")
//    @RequestMapping(value = "/dimensionCategories" , method = RequestMethod.PUT)
//    public MRsDimensionCategory updateDimensionCategory(
//            @ApiParam(name = "dimensionCategory", value = "维度类别", defaultValue = "")
//            @RequestParam(name = "dimensionCategory") String dimensionCategory) throws Exception {
//        return dimensionCategoryClient.updateDimensionCategory(dimensionCategory);
//    }
//
//    @ApiOperation("维度类别删除")
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    public boolean deleteDimensionCategory(
//            @ApiParam(name = "id", value = "维度类别ID", defaultValue = "")
//            @PathVariable(value = "id") String id) throws Exception {
//        return dimensionCategoryClient.deleteDimensionCategory(id);
//    }
//
//    @ApiOperation("维度类别查询")
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    public Page<MRsDimensionCategory> queryCategories(
//            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
//            @RequestParam(name = "fields", required = false) String fields,
//            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
//            @RequestParam(name = "filters", required = false) String filters,
//            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
//            @RequestParam(name = "sorts", required = false) String sorts,
//            @ApiParam(name = "page", value = "页码", defaultValue = "1")
//            @RequestParam(name = "page", required = false) int page,
//            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
//            @RequestParam(name = "size", required = false) int size) throws Exception {
//        return dimensionCategoryClient.queryCategories(fields,filters,sorts,page,size);
//    }
//}
