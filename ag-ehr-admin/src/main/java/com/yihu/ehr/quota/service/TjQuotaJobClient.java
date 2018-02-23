package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2017/6/9.
 */
@FeignClient(name= MicroServices.Quota)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaJobClient {

    @RequestMapping(value = ServiceApi.TJ.FirstExecuteQuota, method = RequestMethod.POST)
    @ApiOperation(value = "初始执行指标任务")
    boolean firstExecuteQuota(@RequestParam("id") Integer id);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExecute, method = RequestMethod.POST)
    @ApiOperation(value = "执行指标任务")
    boolean tjQuotaExecute(@RequestParam(value = "id") Integer id,
                           @RequestParam(value = "startDate", required = false) String startDate,
                           @RequestParam(value = "endDate", required = false) String endDate);

    @RequestMapping(value = ServiceApi.TJ.TjGetQuotaResult, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标执行结果")
    Envelop getQuotaResult(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "pageNo" , required = false) int pageNo,
            @RequestParam(value = "pageSize" , required = false) int pageSize
    );

    @ApiOperation(value = "获取指标统计不同维度结果数据")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaTotalCount, method = RequestMethod.GET)
    Envelop getQuotaTotalCount(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段")
            @RequestParam(value = "dimension", required = false) String dimension);

    @ApiOperation(value = "获取指标统计结果echart图表，支持多条组合")
    @RequestMapping(value = ServiceApi.TJ.GetMoreQuotaGraphicReportPreviews, method = RequestMethod.GET)
    public MChartInfoModel getMoreQuotaGraphicReportPreviews(
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
            @ApiParam(name = "charstr", value = "多图表类型用,拼接,混合类型只支持柱状和线性", defaultValue = "1")
            @RequestParam(value = "charstr" , required = true) String charstr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "视图名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title);

    @ApiOperation(value = "获取指标统计结果echart radar雷达图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaRadarGraphicReportPreviews, method = RequestMethod.GET)
    MChartInfoModel getQuotaRadarGraphicReports(
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title);

    @ApiOperation(value = "获取指标统计结果echart NestedPie旭日图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaNestedPieReportPreviews, method = RequestMethod.GET)
    MChartInfoModel getQuotaNestedPieGraphicReports(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title);

}
