package com.yihu.ehr.dataAnalyze.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.controller.BaseController;
import com.yihu.quota.controller.QuotaReportController;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "大数据分析 - 上卷下钻控制 入口")
public class ReportUpDownController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ReportUpDownController.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotaService quotaService;

    /**
     * 查询结果
     * @return
     */
    @ApiOperation(value = "获取统计报表,二维表数据")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReportTwoDimensionalTable, method = RequestMethod.GET)
    public List<Map<String, Object>> getQuotaReportTwoDimensionalTable(
            @ApiParam(name = "quotaCodeStr", value = "指标Code,多个用,拼接", required = true)
            @RequestParam(value = "quotaCodeStr", required = true) String quotaCodeStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top
    ) {
        QuotaReportController quotaReportController = new QuotaReportController();
        return  quotaReportController.getQuotaReportTwoDimensionalTable(quotaCodeStr,filter,dimension,top);
    }



}
