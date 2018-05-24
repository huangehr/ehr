package com.yihu.ehr.report.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 质控报表
 *
 * @author zhengwei
 * @created 2018.04.24
 */
@FeignClient(name= MicroServices.Analyzer)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PackStatisticsClient {

    @RequestMapping(value =  ServiceApi.StasticReport.GetArchiveReportAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取一段时间内健康档案数据")
    Envelop getArchiveReportAll(@ApiParam(name = "startDate", value = "开始日期")
                                @RequestParam(name = "startDate") String startDate,
                                @ApiParam(name = "endDate", value = "结束日期")
                                @RequestParam(name = "endDate") String endDate,
                                @ApiParam(name = "orgCode", value = "医院代码")
                                @RequestParam(name = "orgCode") String orgCode);

    @RequestMapping(value =  ServiceApi.StasticReport.GetRecieveOrgCount, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    Envelop getRecieveOrgCount(@ApiParam(name = "date", value = "日期")
                               @RequestParam(name = "date") String date);

    @RequestMapping(value =  ServiceApi.StasticReport.GetArchivesInc, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    Envelop getArchivesInc(@ApiParam(name = "date", value = "日期")
                           @RequestParam(name = "date") String date,
                           @ApiParam(name = "orgCode", value = "医院代码")
                           @RequestParam(name = "orgCode", required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesFull, method = RequestMethod.GET)
    @ApiOperation(value = "完整性分析")
    Envelop getArchivesFull(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesTime, method = RequestMethod.GET)
    @ApiOperation(value = "及时性分析")
    Envelop getArchivesTime(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetDataSetCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集数量")
    Envelop getDataSetCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesRight, method = RequestMethod.GET)
    @ApiOperation(value = "准确性分析")
    Envelop getArchivesRight(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetErrorCodeList, method = RequestMethod.GET)
    @ApiOperation(value = "错误数据元列表")
    Envelop getErrorCodeList(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode);
}
