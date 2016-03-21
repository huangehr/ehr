package com.yihu.ehr.ha.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(ApiVersion.Version1_0 )
@ApiIgnore
public interface PlanClient {

    @RequestMapping(value = "/adapter/plans", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    ResponseEntity<Collection<MAdapterPlan>> searchAdapterPlan(
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


    @RequestMapping(value = "/adapter/plan/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    MAdapterPlan getAdapterPlanById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) ;


    @RequestMapping(value = "/adapter/plan", method = RequestMethod.POST)
    @ApiOperation(value = "保存适配方案")
    MAdapterPlan saveAdapterPlan(
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson,
            @ApiParam(name = "isCover", value = "是否覆盖", defaultValue = "")
            @RequestParam(value = "isCover") String isCover) ;


    @RequestMapping(value = "/adapter/plan/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新适配方案")
    MAdapterPlan updateAdapterPlan(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson) ;

    @RequestMapping(value = "/adapter/plans", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案")
    boolean delAdapterPlan(
            @ApiParam(name = "ids", value = "编号列表", defaultValue = "")
            @RequestParam("ids") String ids);

    @RequestMapping(value = "/adapter/plans/list", method = RequestMethod.GET)
    @ApiOperation(value = "根据类型跟版本号获取适配方案列表")
    List<Map<String, String>> getAdapterPlanList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam("type") String type,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) ;


    @RequestMapping(value = "/adapter/plan/{planId}/adapterCustomizes", method = RequestMethod.GET)
    @ApiOperation(value = "获取定制信息")
    Map getAdapterCustomize(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam("version") String version) ;


    @RequestMapping(value = "/adapter/plan/{planId}/adapterDataSet", method = RequestMethod.POST)
    @ApiOperation(value = "定制数据集")
    boolean adapterDataSet(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "customizeData", value = "customizeData", defaultValue = "")
            @RequestParam("customizeData") String customizeData) ;

    /**
     * 适配版本发布
     * 1.生成适配版本文件并记录文件位置；2.修改适配方案状态
     */
    @RequestMapping(value = "/adapter/plan/{planId}/dispatch", method = RequestMethod.POST)
    @ApiOperation(value = "适配版本发布")
    boolean adapterDispatch(
            @ApiParam(name = "planId", value = "方案编号", defaultValue = "")
            @PathVariable("planId") Long planId) ;

}
