package com.yihu.ehr.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.quota.service.OutpatientServiceStatisticClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时报表接口--门急诊服务统计接口 Controller
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "门急诊服务统计接口", tags = {"临时报表接口--门急诊服务统计接口"})
public class OutpatientServiceStatisticController extends BaseController {

    @Autowired
    private OutpatientServiceStatisticClient outpatientServiceStatisticClient;

    @ApiOperation("本月科室门诊人次")
    @RequestMapping(value = ServiceApi.OutpatientServiceStatistic.StatisticDeptOutpatientSum, method = RequestMethod.GET)
    public Envelop statisticDeptOutpatientSum() {
        return outpatientServiceStatisticClient.statisticDeptOutpatientSum();
    }

    @ApiOperation("本月科室转诊人次")
    @RequestMapping(value = ServiceApi.OutpatientServiceStatistic.StatisticDeptTransferTreatmentSum, method = RequestMethod.GET)
    public Envelop statisticDeptTransferTreatmentSum() {
        return outpatientServiceStatisticClient.statisticDeptTransferTreatmentSum();
    }

}
