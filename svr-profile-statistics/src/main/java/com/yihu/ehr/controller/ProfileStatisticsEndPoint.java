package com.yihu.ehr.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @RequestMapping(value = "/profiles/statistics/{orgCode}/createdate", method = RequestMethod.GET)
    @ApiOperation("按入库时间统计")
    public long resolveStatisticsByCreateDate(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {
        try {
            return statisticsService.resolveStatisticsByCreateDate(orgCode, startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    @RequestMapping(value = "/profiles/statistics/{orgCode}/eventdate", method = RequestMethod.GET)
    @ApiOperation("按事件时间统计")
    public long resolveStatisticsByEventDate(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {
        try {
            return statisticsService.resolveStatisticsByEventDate(orgCode, startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    @RequestMapping(value = "/profiles/statistics/{orgCode}/id", method = RequestMethod.GET)
    @ApiOperation("可识别人数统计")
    public long resolveStatisticsByIdNotNull(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {
        try {
            return statisticsService.resolveStatisticsByIdNotNull(orgCode, startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    @RequestMapping(value = "/profiles/statistics/{orgCode}/eventdategroup", method = RequestMethod.GET)
    @ApiOperation("按事件时间分组统计")
    public Map<String, String> resolveStatisticsByEventDateGroup(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate,
            @ApiParam(value = "grap", defaultValue = "+1DAY")
            @RequestParam(value = "grap", defaultValue = "+1DAY") String grap) throws Exception {
        try {
            return statisticsService.resolveStatisticsByEventDateGroup(orgCode, startDate, endDate, grap);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    @RequestMapping(value = "/profiles/statistics/{indexId}", method = RequestMethod.GET)
    @ApiOperation("指标统计查询")
    public Object queryStatistics(
            @ApiParam(value = "indexId")
            @PathVariable(value = "indexId")String indexId,
            @ApiParam(value = "queryParam")
            @RequestParam(value = "queryParam")String queryParam) {
        try {
            JsonNode params = objectMapper.readTree(queryParam);
            return statisticsService.indexStatistics(indexId,params);
        } catch (Exception ex) {
            return null;
        }
    }

}
