package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.analyze.service.pack.PackStatisticsService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 质控报表
 *
 * @author zhengwei
 * @created 2018.04.24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PackStatisticsEndPoint", description = "档案分析服务", tags = {"档案分析服务-质量监控报表"})
public class PackStatisticsEndPoint extends EnvelopRestEndPoint {


    @Autowired
    private PackStatisticsService statisticService;
    @Autowired
    private PackQcReportService packQcReportService;

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveReportAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取一段时间内数据解析情况")
    public Envelop getArchiveReportAll(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode" , required = false) String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        Date start = DateUtil.formatCharDateYMD(startDate);
        Date end = DateUtil.formatCharDateYMD(endDate);
        int day = (int) ((end.getTime() - start.getTime()) / (1000 * 3600 * 24)) + 1;
        List<Map<String, List<Map<String, Object>>>> res = new ArrayList<>();
        for (int i = 0; i < day; i++) {
            Date date = DateUtil.addDate(i, start);
            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            List<Map<String, Object>> list = statisticService.getArchivesCount(DateUtil.toString(date), orgCode);
            map.put(DateUtil.toString(date), list);
            res.add(map);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(res);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetRecieveOrgCount, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    public Envelop getRecieveOrgCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date) throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> list = statisticService.getRecieveOrgCount(date);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(list);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesInc, method = RequestMethod.GET)
    @ApiOperation(value = "获取某天数据新增情况")
    public Envelop getArchivesInc(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> list = statisticService.getArchivesInc(date, orgCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(list);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesFull, method = RequestMethod.GET)
    @ApiOperation(value = "完整性分析")
    public Envelop getArchivesFull(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return statisticService.getArchivesFull(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesTime, method = RequestMethod.GET)
    @ApiOperation(value = "及时性分析")
    public Envelop getArchivesTime(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return statisticService.getArchivesTime(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetDataSetCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集数量")
    public Envelop getDataSetCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.dataSetList(date, date, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesRight, method = RequestMethod.GET)
    @ApiOperation(value = "准确性分析")
    public Envelop getArchivesRight(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return statisticService.getArchivesRight(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetStasticByDay, method = RequestMethod.GET)
    @ApiOperation(value = "app接口")
    public Envelop getStasticByDay(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date) throws Exception {
        return statisticService.getStasticByDay(date);
    }

    @RequestMapping(value = "/stasticReport/getReceiveNum", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集数据")
    public Envelop getReceiveNum(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgArea", value = "区域代码")
            @RequestParam(name = "orgArea", required = false) String orgArea) throws Exception {
        return statisticService.getReceiveNum(startDate, endDate, orgArea);
    }
}