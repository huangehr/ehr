package com.yihu.ehr.government.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by zdm on 2017/12/28.
 */
@FeignClient(name= MicroServices.Quota)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore

public interface OutpatientsBoardClient {

    @ApiOperation("本月门急诊人次")
    @RequestMapping(value = "/statistics/{position}", method = RequestMethod.POST)
    Envelop outpatientsBoardCount(
            @ApiParam(name = "core", value = "集合", required = true)
            @RequestParam(value = "core") String core,
            @ApiParam(name = "position", value = "指标位置", required = true)
            @RequestParam(value = "position") String position);

    @ApiOperation("医院门急诊人次分布")
    @RequestMapping(value = ServiceApi.Government.GetMonthDistribution, method = RequestMethod.POST)
    Envelop getMonthDistribution(
            @ApiParam(name = "core", value = "集合", required = true)
            @RequestParam(value = "core") String core,
            @ApiParam(name = "year", value = "年份", required = true)
            @RequestParam(value = "year") String year);

    @ApiOperation("医院门急诊人次分布")
    @RequestMapping(value = ServiceApi.Government.GetRescue, method = RequestMethod.POST)
    Envelop getRescue();

}
