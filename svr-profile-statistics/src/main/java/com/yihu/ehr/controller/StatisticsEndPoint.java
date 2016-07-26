package com.yihu.ehr.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by lyr on 2016/7/26.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "statistics",description = "档案、人次指标统计")
public class StatisticsEndPoint extends BaseRestEndPoint{

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    StatisticsService statisticsService;

    @RequestMapping(value = "/statistics/profiles",method = RequestMethod.GET)
    @ApiOperation("档案入库统计")
    public Map<String,Object> profileStatistics(
            @RequestParam(value = "items")
            @ApiParam(value = "items")String items,
            @RequestParam(value = "params")
            @ApiParam(value = "params")String params){
        try{
            JsonNode jsonNode = objectMapper.readTree(params);
            return statisticsService.profileStatistics(items,jsonNode);
        }catch (Exception ex){
            return null;
        }
    }

    @RequestMapping(value = "/statistics/outPatientHospital",method = RequestMethod.GET)
    @ApiOperation("档案入库统计")
    public Map<String,Object> outpatientAndHospitalStatistics(
            @RequestParam(value = "items")
            @ApiParam(value = "items")String items,
            @RequestParam(value = "params")
            @ApiParam(value = "params")String params){
        try{
            JsonNode jsonNode = objectMapper.readTree(params);
            return statisticsService.outpatientAndHospitalStatistics(items,jsonNode);
        }catch (Exception ex){
            return null;
        }
    }
}
