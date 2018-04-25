package com.yihu.ehr.report.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.report.service.PackStatisticsClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 质控报表
 *
 * @author zhengwei
 * @created 2018.04.24
 */
@RequestMapping(ApiVersion.Version1_0 +"/admin")
@RestController
@Api(value = "PackStatistics", description = "质量监控报表", tags = {"质量监控报表"})
public class PackStatisticsController extends BaseController {
    @Autowired
    private PackStatisticsClient packStatisticsClient;

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveReportAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取一段时间内数据解析情况")
    public Envelop getArchiveReportAll(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate) {
        Envelop envelop = packStatisticsClient.getArchiveReportAll(startDate,endDate);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetRecieveOrgCount, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    public Envelop getRecieveOrgCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date) {
        Envelop envelop = packStatisticsClient.getRecieveOrgCount(date);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesInc, method = RequestMethod.GET)
    @ApiOperation(value = "获取某天数据新增情况")
    public Envelop getArchivesInc(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        Envelop envelop = packStatisticsClient.getArchivesInc(date,orgCode);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesFull, method = RequestMethod.GET)
    @ApiOperation(value = "完整性分析")
    public Envelop getArchivesFull(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        Envelop envelop = packStatisticsClient.getArchivesFull(startDate,endDate,orgCode);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesTime, method = RequestMethod.GET)
    @ApiOperation(value = "及时性分析")
    public Envelop getArchivesTime(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        return packStatisticsClient.getArchivesTime(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetDataSetCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集数量")
    public Envelop getDataSetCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        return packStatisticsClient.getDataSetCount(date, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesRight, method = RequestMethod.GET)
    @ApiOperation(value = "准确性分析")
    public Envelop getArchivesRight(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        return packStatisticsClient.getArchivesRight(startDate, endDate, orgCode);
    }
}
