//package com.yihu.ehr.resource.client;
//
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.constants.MicroServices;
//import com.yihu.ehr.model.resource.MRsDimensionCategory;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.data.domain.Page;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import springfox.documentation.annotations.ApiIgnore;
//
///**
// * @author linaz
// * @created 2016.05.23 17:11
// */
//@FeignClient(name = MicroServices.Resource)
//@RequestMapping(value = ApiVersion.Version1_0)
//@ApiIgnore
//public interface DimensionCategoryClient {
//
//    @ApiOperation("创建维度类别")
//    @RequestMapping(method = RequestMethod.POST)
//    MRsDimensionCategory createDimensionCategory(
//            @RequestParam(name = "dimensionCategory") String dimensionCategory);
//
//    @ApiOperation("更新维度类别")
//    @RequestMapping(method = RequestMethod.PUT)
//    MRsDimensionCategory updateDimensionCategory(
//            @RequestParam(name = "dimensionCategory") String dimensionCategory);
//
//    @ApiOperation("维度类别删除")
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    boolean deleteDimensionCategory(
//            @PathVariable(value = "id") String id);
//
//    @ApiOperation("维度类别查询")
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    Page<MRsDimensionCategory> queryCategories(
//            @RequestParam(name = "fields", required = false) String fields,
//            @RequestParam(name = "filters", required = false) String filters,
//            @RequestParam(name = "sorts", required = false) String sorts,
//            @RequestParam(name = "page", required = false) int page,
//            @RequestParam(name = "size", required = false) int size);
//
//}
