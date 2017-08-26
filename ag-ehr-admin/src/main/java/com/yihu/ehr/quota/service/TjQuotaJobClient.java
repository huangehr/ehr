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

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExecute, method = RequestMethod.GET)
    @ApiOperation(value = "执行指标任务")
    boolean tjQuotaExecute(@RequestParam("id") Integer id);

    @RequestMapping(value = ServiceApi.TJ.TjGetQuotaResult, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标执行结果")
    Envelop getQuotaResult(
            @RequestParam(value = "id" , required = true) int id,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "pageNo" , required = false) int pageNo,
            @RequestParam(value = "pageSize" , required = false) int pageSize
    );

    @ApiOperation(value = "获取指标统计结果曲线性或柱状报表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaGraphicReportPreview, method = RequestMethod.GET)
    MChartInfoModel getQuotaGraphicReport(
            @RequestParam(value = "id" , required = true) int id,
            @RequestParam(value = "type" , required = true) int type,
            @RequestParam(value = "filters", required = false) String filters);

    @ApiOperation(value = "获取指标统计不同维度结果总量")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaTotalCount, method = RequestMethod.GET)
    Envelop getQuotaTotalCount(
            @RequestParam(value = "id" , required = true) int id,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "dimension", required = false) String dimension);
}
