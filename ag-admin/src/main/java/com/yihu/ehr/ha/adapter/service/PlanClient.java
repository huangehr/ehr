package com.yihu.ehr.ha.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@FeignClient(MicroServices.AdaptionMgr)
@RequestMapping(ApiVersion.Version1_0  + "/adapter")
@ApiIgnore
public interface PlanClient {


    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    Collection<MAdapterPlan> searchAdapterPlan(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) ;


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    public MAdapterPlan getAdapterPlanById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) throws Exception ;


    @RequestMapping(value = "/plan", method = RequestMethod.POST)
    @ApiOperation(value = "保存适配方案")
    public MAdapterPlan saveAdapterPlan(
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson,
            @ApiParam(name = "isCover", value = "是否覆盖", defaultValue = "")
            @RequestParam(value = "isCover") String isCover) throws Exception ;


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新适配方案")
    public MAdapterPlan updateAdapterPlan(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson) throws Exception ;


    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案")
    public boolean delAdapterPlan(
            @ApiParam(name = "ids", value = "编号列表", defaultValue = "")
            @RequestParam("ids") String ids) throws Exception ;


    @RequestMapping(value = "/plans/list", method = RequestMethod.GET)
    @ApiOperation(value = "根据类型跟版本号获取适配方案列表")
    public List<Map<String, String>> getAdapterPlanList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam("type") String type,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception ;


    @RequestMapping(value = "/plan/{planId}/adapterCustomizes", method = RequestMethod.GET)
    @ApiOperation(value = "获取定制信息")
    public Map getAdapterCustomize(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam("version") String version) throws Exception ;


    @RequestMapping(value = "/plan/{planId}/adapterDataSet", method = RequestMethod.POST)
    @ApiOperation(value = "定制数据集")
    public boolean adapterDataSet(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "customizeData", value = "customizeData", defaultValue = "")
            @RequestParam("customizeData") String customizeData) throws Exception ;
}
