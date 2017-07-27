package com.yihu.ehr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by lyr on 2016/7/21.
 */
@RestController
@Api(value = "profile-statistics", description = "档案统计")
@RequestMapping(value = ApiVersion.Version1_0)
public class ProfileStatisticsEndPoint extends BaseRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StatisticsService statisticsService;

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileCreateDate, method = RequestMethod.GET)
    @ApiOperation("按入库时间统计")
    public Map<String, Map<String, Long>> resolveStatisticsByCreateDate(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {
        try {
            return statisticsService.resolveStatisticsByCreateDateOrEventDate(orgCode, startDate, endDate, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileEventDate, method = RequestMethod.GET)
    @ApiOperation("按事件时间统计")
    public Map<String, Map<String, Long>> resolveStatisticsByEventDate(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {
        try {
            return statisticsService.resolveStatisticsByCreateDateOrEventDate(orgCode, startDate, endDate, 2);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileIdNotNull, method = RequestMethod.GET)
    @ApiOperation("可识别人数统计")
    public Map<String, Map<String, Long>> resolveStatisticsByIdNotNull(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {
        try {
            return statisticsService.statisticsByIdNotNull(orgCode, startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileEventDateGroup, method = RequestMethod.GET)
    @ApiOperation("按事件时间分组统计")
    public Map<String, String> resolveStatisticsByEventDateGroup(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate,
            @ApiParam(value = "eventType")
            @RequestParam(value = "eventType", required = false) String eventType,
            @ApiParam(value = "grap", defaultValue = "+1DAY")
            @RequestParam(value = "grap", defaultValue = "+1DAY") String grap) throws Exception {
        try {
            return statisticsService.resolveStatisticsByEventDateGroup(orgCode, startDate, endDate, grap, eventType);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}
