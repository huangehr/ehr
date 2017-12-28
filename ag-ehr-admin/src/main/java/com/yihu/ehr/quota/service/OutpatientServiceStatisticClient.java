package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 临时报表接口--门急诊服务统计接口 Client
 */
@FeignClient(name = MicroServices.Quota)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface OutpatientServiceStatisticClient {

    @ApiOperation("本月科室门诊人次")
    @RequestMapping(value = ServiceApi.OutpatientServiceStatistic.StatisticDeptOutpatientSum, method = RequestMethod.GET)
    public Envelop statisticDeptOutpatientSum();

    @ApiOperation("本月科室转诊人次")
    @RequestMapping(value = ServiceApi.OutpatientServiceStatistic.StatisticDeptTransferTreatmentSum, method = RequestMethod.GET)
    public Envelop statisticDeptTransferTreatmentSum();

}
