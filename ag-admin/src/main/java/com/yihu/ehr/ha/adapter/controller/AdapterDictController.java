package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/adapterDict")
@RestController
public class AdapterDictController{
// extends BaseRestController
    @RequestMapping(value = "/adapterDict",method = RequestMethod.GET)
    public Object getAdapterDictById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "apiVersion") String apiVersion,
                                     @ApiParam(name = "id",value = "适配关系ID")
                                     @RequestParam(value = "id")String id){
        return null;
    }

    @RequestMapping(value = "/adapterDicts",method = RequestMethod.GET)
    public String getAdapterDictsByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                              @PathVariable(value = "apiVersion") String apiVersion,
                                              @ApiParam(name = "adapterPlanId", value = "方案ID")
                                              @RequestParam(value = "adapterPlanId") long adapterPlanId,
                                              @ApiParam(name = "code", value = "字典代码")
                                              @RequestParam(value = "code") String code,
                                              @ApiParam(name = "name", value = "字典名称")
                                              @RequestParam(value = "name") String name,
                                              @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                              @RequestParam(value = "page") int page,
                                              @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                              @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/adapterDictEntrys",method = RequestMethod.GET)
    public String getAdapterDictEntryByDictId(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                              @PathVariable(value = "apiVersion") String apiVersion,
                                              @ApiParam(name = "adapterPlanId", value = "方案ID")
                                              @RequestParam(value = "adapterPlanId") long adapterPlanId,
                                              @ApiParam(name = "dictId", value = "标准字典")
                                              @RequestParam(value = "dictId") long dictId,
                                              @ApiParam(name = "code", value = "字典项代码")
                                              @RequestParam(value = "code") String code,
                                              @ApiParam(name = "name", value = "字典项值")
                                              @RequestParam(value = "name") String name,
                                              @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                              @RequestParam(value = "page") int page,
                                              @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                              @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/adapterDictEntry",method = RequestMethod.GET)
    public String getAdapterDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "apiVersion") String apiVersion,
                                      @ApiParam(name = "id", value = "适配关系ID")
                                      @RequestParam(value = "id") long id) {
        return null;
    }

    @RequestMapping(value = "/adapterDictEntry",method = RequestMethod.POST)
    public String updateAdapterDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                         @PathVariable(value = "apiVersion") String apiVersion,
                                         @ApiParam(name = "id", value = "适配关系ID")
                                         @RequestParam(value = "id") long id,
                                         @ApiParam(name = "planId", value = "方案ID")
                                         @RequestParam(value = "planId") String planId,
                                         @ApiParam(name = "dictId", value = "标准字典ID")
                                         @RequestParam(value = "dictId") String dictId,
                                         @ApiParam(name = "dictEntryId", value = "标准字典项ID")
                                         @RequestParam(value = "dictEntryId") String dictEntryId,
                                         @ApiParam(name = "orgDictId", value = "机构字典ID")
                                         @RequestParam(value = "orgDictId") String orgDictId,
                                         @ApiParam(name = "orgDictEntryId", value = "机构字典项ID")
                                         @RequestParam(value = "orgDictEntryId") String orgDictEntryId,
                                         @ApiParam(name = "description", value = "说明")
                                         @RequestParam(value = "description") String description) {
        return null;
    }

    @RequestMapping(value = "/adapterDictEntry",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配关系", produces = "application/json", notes = "根据适配关系ID删除适配关系，批量删除时ID以逗号隔开")
    public String delAdapterDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion,
                               @ApiParam(name = "ids", value = "适配关系ID")
                                   @RequestParam(value = "ids") long ids){
        return null;
    }

    /**
     * 标准字典项的下拉
     * @return
     */
    @RequestMapping(value = "/getStdDictEntry",method = RequestMethod.GET)
    @ApiOperation(value = "获取标准字典项", produces = "application/json", notes = "获取未适配的标准数据元，查询条件(mode)为 modify/view")
    public Object getStdDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "adapterPlanId", value = "方案ID")
                                  @RequestParam(value = "adapterPlanId") long adapterPlanId,
                                  @ApiParam(name = "dictId", value = "标准字典ID")
                                  @RequestParam(value = "dictId") long dictId,
                                  @ApiParam(name = "mode", value = "查询条件")
                                  @RequestParam(value = "mode") String mode) {
        return null;
    }

    @RequestMapping(value = "/getOrgDict", method = RequestMethod.GET)
    public Object getOrgDict(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "apiVersion") String apiVersion,
                             @ApiParam(name = "adapterPlanId", value = "方案ID")
                             @RequestParam(value = "adapterPlanId") long adapterPlanId) {
        return null;
    }

    @RequestMapping("/getOrgDictEntry")
    public Object getOrgDictEntry(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "adapterPlanId", value = "方案ID")
                                      @RequestParam(value = "adapterPlanId") long adapterPlanId,
                                  @ApiParam(name = "orgDictId",value = "机构字典ID")
                                   @RequestParam(value = "orgDictId") int orgDictId){
        return null;
    }


}
