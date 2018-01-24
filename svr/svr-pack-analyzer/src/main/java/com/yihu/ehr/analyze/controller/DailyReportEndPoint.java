package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.DailyReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DailyReportEndPoint", description = "档案日报上传", tags = {"档案分析服务-档案日报上传"})
public class DailyReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DailyReportService dailyReportService;

    @ApiOperation(value = "档案日报上传")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.DailyReport, method = RequestMethod.POST)
    public Envelop dailyReport(
            @ApiParam(name = "report", value = "日报json对象")
            @RequestParam(value = "report", required = true) String report) {
       return dailyReportService.dailyReport(report);
    }

    @ApiOperation(value = "档案日报查询")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.List, method = RequestMethod.POST)
    public Envelop list(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter) {
            return dailyReportService.list(filter);
    }

    @ApiOperation(value = "根据某个字段查询档案")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.FindByField, method = RequestMethod.POST)
    public Envelop findByField(
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") String value) {
        return dailyReportService.findByField(field, value);
    }
}
