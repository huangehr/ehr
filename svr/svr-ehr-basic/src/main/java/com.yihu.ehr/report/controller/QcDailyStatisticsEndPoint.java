package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.report.service.QcDailyReportDetailService;
import com.yihu.ehr.report.service.QcDailyReportService;
import com.yihu.ehr.report.service.QcDailyStatisticsService;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "qcDailyStatistics", description = "档案统计", tags = {"档案统计"})
public class QcDailyStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    QcDailyStatisticsService qcDailyStatisticsService;

    @RequestMapping(value = ServiceApi.Report.QcDailyStatisticsStorage, method = RequestMethod.GET)
    @ApiOperation(value = "单个机构总入库统计")
    public Map<String,Integer> getQcDailyStatisticsStorage(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode", required = false) String orgCode) throws Exception {

        Map<String,Integer> map = new HashMap<>();
        int total = 0;
        List<Object> list = qcDailyStatisticsService.getQcDailyStatisticsStorageGroupEventType(orgCode);
        for(int i = 0 ;i< list.size() ; i++){
            Object[] obs = (Object[]) list.get(i);
            map.put(obs[1].toString(),Integer.valueOf(obs[0].toString()));
            total = total + Integer.valueOf(obs[0].toString());
        }
        map.put("total",total);
        return map;
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyStatisticsIdentify, method = RequestMethod.GET)
    @ApiOperation(value = "单个机构总身份识别统计")
    public Map<String,String> getQcDailyStatisticsIdentify(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode", required = false) String orgCode) throws Exception {

        Map<String,String> map = new HashMap<>();
        int isTotal = 0;
        int noTotal = 0;
        List<Object> list = qcDailyStatisticsService.getQcDailyStatisticsIdentify(orgCode);
        for(int i = 0 ;i< list.size() ; i++){
            Object[] obs = (Object[]) list.get(i);
            map.put(obs[2].toString(),obs[0].toString() + ";" + obs[1].toString());
            isTotal = isTotal + Integer.valueOf(obs[0].toString());
            noTotal = noTotal + Integer.valueOf(obs[1].toString());
        }
        map.put("total",String.valueOf(isTotal) + ";" + String.valueOf(noTotal));
        return map;
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyStatisticsStorageByDate, method = RequestMethod.GET)
    @ApiOperation(value = "单个机构入库按时间统计")
    public List getQcDailyStatisticsStorageByDate(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "startDate", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "截止日期", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate) throws Exception {

        List list  = qcDailyStatisticsService.getQcDailyStatisticsStorageByDate(orgCode,startDate,endDate);
        return list;
    }







}
