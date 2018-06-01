package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.analyze.service.pack.PackStatisticsService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
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
 * @Author: zhengwei
 * @Date: 2018/5/31 16:20
 * @Description: 质控报表
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PackQcReportEndPoint", description = "新质控管理报表", tags = {"新质控管理报表"})
public class PackQcReportEndPoint {

    @Autowired
    private PackQcReportService packQcReportService;

    @RequestMapping(value = ServiceApi.PACKQCREPORT.dailyReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取医院数据")
    public Envelop dailyReport(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.dailyReport(startDate, endDate, orgCode);
    }
}
