package com.yihu.ehr.singledisease.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wxw on 2018/2/27.
 */
@FeignClient(name = MicroServices.Quota)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface SingleDiseaseClient {

    @RequestMapping(value = ServiceApi.TJ.GetHeatMap, method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    Envelop getHeatMap(@RequestParam(value = "condition", required = false) String condition);

    @RequestMapping(value = ServiceApi.TJ.GetNumberOfDiabetes, method = RequestMethod.GET)
    @ApiOperation(value = "糖尿病患者数")
    Envelop getNumberOfDiabetes(@RequestParam(value = "condition", required = false) String condition);

    @RequestMapping(value = ServiceApi.TJ.GetPieData, method = RequestMethod.GET)
    @ApiOperation(value = "获取饼图数据")
    Envelop getPieData(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "condition", required = false) String condition);

    @RequestMapping(value = ServiceApi.TJ.GetLineData, method = RequestMethod.GET)
    @ApiOperation(value = "获取折线图数据")
    Envelop getLineData(@RequestParam(value = "condition", required = false) String condition);

    @RequestMapping(value = ServiceApi.TJ.GetBarData, method = RequestMethod.GET)
    @ApiOperation(value = "获取柱状图数据")
    Envelop getBarData(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "condition", required = false) String condition);

    @RequestMapping(value = ServiceApi.TJ.GetDiseaseTypeAnalysisInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取疾病类型分析数据")
    Envelop getDiseaseTypeInfo(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "filter", required = false) String filter);

    @RequestMapping(value = ServiceApi.TJ.GetSexAnalysisInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取疾病类型分析数据")
    Envelop getSexAnalysisInfo(
            @RequestParam(value = "type", defaultValue = "1") String type,
            @RequestParam(value = "filter", required = false) String filter);

    @RequestMapping(value = ServiceApi.TJ.GetAgeAnalysisInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取疾病类型分析数据")
    Envelop getAgeAnalysisInfo(
            @RequestParam(value = "type", defaultValue = "1") String type,
            @RequestParam(value = "filter", required = false) String filter);

    @RequestMapping(value = ServiceApi.SingleDisease.GetDropdownList, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症和药品查询下拉列表前十 数据")
    Envelop getDropdownList(
            @RequestParam(value = "type" ,required =  true ) String type);

    @RequestMapping(value = ServiceApi.SingleDisease.GetSymptomDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症详细查询页 数据")
    Object getSymptomDetailData(
            @RequestParam(value = "name" ,required = false ,defaultValue = "") String name);

    @RequestMapping(value = ServiceApi.SingleDisease.GetMedicineDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取药品详细查询页 数据")
    Object getMedicineDetailData(
            @RequestParam(value = "name" ,required =  false, defaultValue = "") String name);
}
