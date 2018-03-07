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

/**
 * Created by wxw on 2018/2/27.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "SingleDiseaseController", description = "单病种", tags = {"单病种-数据分析"})
public class SingleDiseaseController extends EnvelopRestEndPoint {
    @Autowired
    private SingleDiseaseClient singleDiseaseClient;

    @RequestMapping(value = ServiceApi.TJ.GetHeatMapByQuotaCode, method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    public Envelop getHeatMap() {
        Envelop envelop = singleDiseaseClient.getHeatMap();
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetNumberOfDiabetes, method = RequestMethod.GET)
    @ApiOperation(value = "糖尿病患者数")
    public Envelop getNumberOfDiabetes() {
        Envelop envelop = singleDiseaseClient.getNumberOfDiabetes();
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetPieData, method = RequestMethod.GET)
    @ApiOperation(value = "获取饼图数据")
    public Envelop getPieData(
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code") String code) {
        return singleDiseaseClient.getPieData(type, code);
    }

    @RequestMapping(value = ServiceApi.TJ.GetLineData, method = RequestMethod.GET)
    @ApiOperation(value = "获取折线图数据")
    public Envelop getLineData() {
        return singleDiseaseClient.getLineData();
    }

    @RequestMapping(value = ServiceApi.TJ.GetBarData, method = RequestMethod.GET)
    @ApiOperation(value = "获取柱状图数据")
    public Envelop getBarData(
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type) {
        return singleDiseaseClient.getBarData(type);
    }
}
