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

    @RequestMapping(value = ServiceApi.TJ.GetHeatMapByQuotaCode, method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    Envelop getHeatMap(@RequestParam(value = "quotaCode") String quotaCode);

    @RequestMapping(value = ServiceApi.TJ.GetNumberOfDiabetes, method = RequestMethod.GET)
    @ApiOperation(value = "糖尿病患者数")
    Envelop getNumberOfDiabetes(@RequestParam(value = "quotaCode") String quotaCode);

    @RequestMapping(value = ServiceApi.TJ.GetPieData, method = RequestMethod.GET)
    @ApiOperation(value = "获取饼图数据")
    Envelop getPieData(
            @RequestParam(value = "quotaCode") String quotaCode);

    @RequestMapping(value = ServiceApi.TJ.GetLineData, method = RequestMethod.GET)
    @ApiOperation(value = "获取折线图数据")
    Envelop getLineData(
            @RequestParam(value = "quotaCode") String quotaCode);
    @RequestMapping(value = ServiceApi.TJ.GetBarData, method = RequestMethod.GET)
    @ApiOperation(value = "获取柱状图数据")
    Envelop getBarData(
            @RequestParam(value = "quotaCode") String quotaCode,
            @RequestParam(value = "type") String type);
}
