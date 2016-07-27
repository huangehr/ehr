package com.yihu.ehr.controller;

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
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "OutPatient-Statistics", description = "门诊统计")
public class OutPatientStatisticsEndPoint extends BaseRestEndPoint {

    @Autowired
    StatisticsService statisticsService;

    @RequestMapping(value = "/outpatient/statistics/{orgCode}/dept", method = RequestMethod.GET)
    @ApiOperation("不同科室门诊统计")
    public Map<String, Long> getOutPatientStatisticsByDept(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.groupStatistics("EHR_000082", orgCode, eventDate, "0",true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/outpatient/statistics/{orgCode}/sex", method = RequestMethod.GET)
    @ApiOperation("不同性别门诊统计")
    public Map<String, Long> getOutPatientStatisticsBySex(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.groupStatistics("EHR_000019_VALUE", orgCode, eventDate, "0",true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/outpatient/statistics/{orgCode}", method = RequestMethod.GET)
    @ApiOperation("门诊统计")
    public long getOutPatientStatistics(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.filterQueryStatistics(orgCode, eventDate, "0");
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

}
