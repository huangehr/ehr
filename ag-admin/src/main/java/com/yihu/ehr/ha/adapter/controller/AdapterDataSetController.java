package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/adapterSet")
@RestController
public class AdapterDataSetController  {
//extends BaseRestController
    @RequestMapping(value = "/adapterMetaData",method = RequestMethod.GET)
    public Object getAdapterMetaDataById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                         @PathVariable(value = "apiVersion") String apiVersion,
                                         @ApiParam(name = "id",value = "适配关系ID")
                                         @RequestParam(value = "id")String id){
        return null;
    }

    @RequestMapping(value = "/adapterDataSets",method = RequestMethod.GET)
    public String getAdapterDataSetByPlanId(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                            @PathVariable(value = "apiVersion") String apiVersion,
                                            @ApiParam(name = "planId", value = "适配方案ID")
                                            @RequestParam(value = "planId") String planId,
                                            @ApiParam(name = "code", value = "数据集代码")
                                            @RequestParam(value = "code") String code,
                                            @ApiParam(name = "name", value = "数据集名称")
                                            @RequestParam(value = "name") String name,
                                            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                            @RequestParam(value = "page") int page,
                                            @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                            @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/adapterMetaDatas",method = RequestMethod.GET)
    public String getAdapterMetaDataByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                                 @PathVariable(value = "apiVersion") String apiVersion,
                                                 @ApiParam(name = "planId", value = "适配方案ID")
                                                 @RequestParam(value = "planId") long planId,
                                                 @ApiParam(name = "dataSetId", value = "数据集ID")
                                                 @RequestParam(value = "dataSetId") long dataSetId,
                                                 @ApiParam(name = "code", value = "数据元代码")
                                                 @RequestParam(value = "code") String code,
                                                 @ApiParam(name = "name", value = "数据元名称")
                                                 @RequestParam(value = "name") String name,
                                                 @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                                 @RequestParam(value = "page") int page,
                                                 @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                                 @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/adapterMetaData",method = RequestMethod.POST)
    public Object saveAdapterMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "apiVersion") String apiVersion,
                                      @ApiParam(name = "planId", value = "适配方案ID")
                                      @RequestParam(value = "planId") long planId,
                                      @ApiParam(name = "",value = "")
                                      @RequestParam(value = "")String metaDataId,
                                      @ApiParam(name = "",value = "")
                                      @RequestParam(value = "")String DataSetId,
                                      @ApiParam(name = "",value = "")
                                      @RequestParam(value = "")String orgMetaDataId,
                                      @ApiParam(name = "",value = "")
                                      @RequestParam(value = "")String orgDataSetId,
                                      @ApiParam(name = "",value = "")
                                      @RequestParam(value = "")String dataType,
                                      @ApiParam(name = "description",value = "说明")
                                      @RequestParam(value = "description")String description){
        return null;
    }

    @RequestMapping(value = "/adapterMetaData",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配关系", produces = "application/json", notes = "根据适配关系ID删除适配关系，批量删除时ID以逗号隔开")
    public String deleteAdapterMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                        @PathVariable(value = "apiVersion") String apiVersion,
                                        @ApiParam(name = "id", value = "适配关系ID")
                                        @RequestParam(value = "ids") String ids) {
        return null;
    }

    @RequestMapping(value = "/getStdMetaData",method = RequestMethod.GET)
    @ApiOperation(value = "获取标准数据元", produces = "application/json", notes = "获取未适配的标准数据元，查询条件(mode)为 modify/view")
    public Object getStdMetaData(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "方案ID")
            @RequestParam(value = "adapterPlanId") long adapterPlanId,
            @ApiParam(name = "dataSetId", value = "标准数据集ID")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "mode", value = "查询条件")
            @RequestParam(value = "mode") String mode) {
        return null;
    }

    @RequestMapping(value = "/getOrgDataSet", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构数据集", produces = "application/json", notes = "获取机构数据集")
    public Object getOrgDataSet(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "adapterPlanId", value = "方案ID")
                                @RequestParam(value = "adapterPlanId") long adapterPlanId) {
        return null;
    }

    @RequestMapping("/getOrgMetaData")
    @ApiOperation(value = "获取未适配的机构数据元", produces = "application/json", notes = "获取未适配的机构数据元")
    public Object getOrgMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "orgDataSetSeq", value = "机构数据集序号")
                                 @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
                                 @ApiParam(name = "adapterPlanId", value = "适配方案ID")
                                 @RequestParam(value = "adapterPlanId") Long adapterPlanId) {
        return null;
    }
}
