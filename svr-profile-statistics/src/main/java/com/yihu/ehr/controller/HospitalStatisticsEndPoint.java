package com.yihu.ehr.controller;

import com.yihu.ehr.api.ServiceApi;
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
@Api(value = "Hospital-Statistics", description = "住院统计")
public class HospitalStatisticsEndPoint extends BaseRestEndPoint {

    @Autowired
    StatisticsService statisticsService;

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsHospitalizationDept, method = RequestMethod.GET)
    @ApiOperation("不同科室出院统计")
    public Map<String, Long> getOutPatientStatisticsByDept(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.groupStatistics("EHR_000223", orgCode, eventDate, "1",true,false);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsHospitalizationSex, method = RequestMethod.GET)
    @ApiOperation("不同性别出院统计")
    public Map<String, Long> getOutPatientStatisticsBySex(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.groupStatistics("EHR_000019_VALUE", orgCode, eventDate, "1",true,false);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsHospitalizationDisease, method = RequestMethod.GET)
    @ApiOperation("不同疾病出院统计")
    public Map<String, Long> getOutPatientStatisticsByDisease(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.groupStatistics("EHR_000163_VALUE", orgCode, eventDate, "1",true,false);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsHospitalization, method = RequestMethod.GET)
    @ApiOperation("出院统计")
    public long getOutPatientStatistics(
            @ApiParam(value = "orgCode")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(value = "eventDate")
            @RequestParam(value = "eventDate") String eventDate) throws Exception {
        try {
            return statisticsService.filterQueryStatistics(orgCode, eventDate, "1");
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

}
