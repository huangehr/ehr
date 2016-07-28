package com.yihu.ehr.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.dao.model.DailyMonitorFile;
import com.yihu.ehr.model.profilestatistics.MDailyMonitorFile;
import com.yihu.ehr.service.DailyMonitorService;
import com.yihu.ehr.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    @Autowired
    DailyMonitorService dailyMonitorService;

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfile,method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsOutpatientHospital,method = RequestMethod.GET)
    @ApiOperation("门诊住院统计")
    public Map<String,Object> outpatientAndHospitalStatistics(
            @RequestParam(value = "items")
            @ApiParam(value = "items")String items,
            @RequestParam(value = "params")
            @ApiParam(value = "params")String params){
        try{
            JsonNode jsonNode = objectMapper.readTree(params);
            return statisticsService.outpatientAndHospitalStatistics(items,jsonNode,false);
        }catch (Exception ex){
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsDailyReport,method = RequestMethod.GET)
    @ApiOperation("日常监测统计生成")
    public DailyMonitorFile generateDailyReport(
            @ApiParam(value = "date")
            @RequestParam(value = "date") String date){
        try{
            return statisticsService.generateDailyReportFile(date);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsDailyReportFiles,method = RequestMethod.GET)
    @ApiOperation("查询日常监测文件")
    public List<MDailyMonitorFile> getMetadata(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(name="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(name="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(name="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(name="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(name="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long total = 0;
        Collection<MDailyMonitorFile> metaList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<DailyMonitorFile> metadataPage = dailyMonitorService.findByPageAndSort(reducePage(page),size,sorts);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(),new ArrayList<>(metadataPage.getNumber()),MDailyMonitorFile.class,fields);
        }
        else
        {
            List<DailyMonitorFile> metadata = dailyMonitorService.search(fields,filters,sorts,page,size);
            total = dailyMonitorService.getCount(filters);
            metaList = convertToModels(metadata,new ArrayList<>(metadata.size()),MDailyMonitorFile.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return (List<MDailyMonitorFile>)metaList;
    }
}
