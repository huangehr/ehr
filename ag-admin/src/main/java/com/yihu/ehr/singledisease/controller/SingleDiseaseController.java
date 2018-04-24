package com.yihu.ehr.singledisease.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.singledisease.service.SingleDiseaseClient;
import com.yihu.ehr.util.rest.Envelop;
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
 * Created by wxw on 2018/2/27.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "SingleDiseaseController", description = "单病种", tags = {"单病种-数据分析"})
public class SingleDiseaseController extends EnvelopRestEndPoint {
    @Autowired
    private SingleDiseaseClient singleDiseaseClient;

    @RequestMapping(value = ServiceApi.TJ.GetHeatMap, method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    public Envelop getHeatMap(
            @ApiParam(name = "condition", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "condition", required = false) String condition) {
        Envelop envelop = singleDiseaseClient.getHeatMap(condition);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetNumberOfDiabetes, method = RequestMethod.GET)
    @ApiOperation(value = "糖尿病患者数")
    public Envelop getNumberOfDiabetes(
            @ApiParam(name = "condition", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "condition", required = false) String condition) {
        Envelop envelop = singleDiseaseClient.getNumberOfDiabetes(condition);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetPieData, method = RequestMethod.GET)
    @ApiOperation(value = "获取饼图数据")
    public Envelop getPieData(
            @ApiParam(name = "type", required = true, value = "类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "condition", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "condition", required = false) String condition) {
        return singleDiseaseClient.getPieData(type, condition);
    }

    @RequestMapping(value = ServiceApi.TJ.GetLineData, method = RequestMethod.GET)
    @ApiOperation(value = "获取折线图数据")
    public Envelop getLineData(
            @ApiParam(name = "condition", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "condition", required = false) String condition) {
        return singleDiseaseClient.getLineData(condition);
    }

    @RequestMapping(value = ServiceApi.TJ.GetBarData, method = RequestMethod.GET)
    @ApiOperation(value = "获取柱状图数据")
    public Envelop getBarData(
            @ApiParam(name = "type", required = true, value = "类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "condition", value = "过滤条件", defaultValue = "")
            @RequestParam(value = "condition", required = false) String condition) {
        return singleDiseaseClient.getBarData(type, condition);
    }

    @RequestMapping(value = ServiceApi.TJ.GetDiseaseTypeAnalysisInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取疾病类型分析数据")
    public Envelop getDiseaseTypeInfo(
            @ApiParam(name = "type", required = true, value = "1 年份 2 月趋势")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "filter", value = "过滤的年份")
            @RequestParam(value = "filter", required = false) String filter) {
        return singleDiseaseClient.getDiseaseTypeInfo(type, filter);
    }

    @RequestMapping(value = ServiceApi.TJ.GetSexAnalysisInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取疾病类型分析数据")
    public Envelop getSexAnalysisInfo(
            @ApiParam(name = "type", required = true, value = "1 年份 2 月趋势")
            @RequestParam(value = "type", defaultValue = "1") String type,
            @ApiParam(name = "filter", value = "过滤的年份")
            @RequestParam(value = "filter", required = false) String filter) {
        return singleDiseaseClient.getSexAnalysisInfo(type, filter);
    }

    @RequestMapping(value = ServiceApi.TJ.GetAgeAnalysisInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取疾病类型分析数据")
    public Envelop getAgeAnalysisInfo(
            @ApiParam(name = "type", required = true, value = "1 年份 2 月趋势")
            @RequestParam(value = "type", defaultValue = "1") String type,
            @ApiParam(name = "filter", value = "过滤的年份")
            @RequestParam(value = "filter", required = false) String filter) {
        return singleDiseaseClient.getAgeAnalysisInfo(type, filter);
    }


    @RequestMapping(value = ServiceApi.SingleDisease.GetDropdownList, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症和药品查询下拉列表前十 数据")
    public Envelop getDropdownList(
            @ApiParam(name = "type", value = "类型 1并发症 2 药品")
            @RequestParam(value = "type" ,required =  true ) String type) throws Exception {
        return singleDiseaseClient.getDropdownList(type);
    }

    @RequestMapping(value = ServiceApi.SingleDisease.GetSymptomDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症详细查询页 数据")
    public Object getSymptomDetailData(
            @ApiParam(name = "name", value = "并发症名称")
            @RequestParam(value = "name" ,required = false ,defaultValue = "") String name) throws Exception {
        return singleDiseaseClient.getSymptomDetailData(name);
    }

    @RequestMapping(value = ServiceApi.SingleDisease.GetMedicineDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取药品详细查询页 数据")
    public Object getMedicineDetailData(
            @ApiParam(name = "name", value = "药品名称")
            @RequestParam(value = "name" ,required =  false, defaultValue = "") String name) throws Exception {
        return singleDiseaseClient.getMedicineDetailData(name);
    }

}
