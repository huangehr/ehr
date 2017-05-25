package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.report.service.QcDailyReportClient;
import com.yihu.ehr.report.service.QcDailyStatisticsClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "StatisticsProfile", description = "档案统计", tags = {"档案统计-档案统计"})
public class QcDailyStatisticsController extends ExtendController<QcDailyReport> {

    @Autowired
    QcDailyStatisticsClient qcDailyStatisticsClient;


    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileCreateDate, method = RequestMethod.GET)
    @ApiOperation(value = "按入库时间统计")
    public Map<String, Map<String, Long>> search(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {

         Map<String, Map<String, Long>> map = qcDailyStatisticsClient.search(orgCode, startDate, endDate );
        return map;
    }



}
