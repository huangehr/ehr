package com.yihu.ehr.report.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@FeignClient(name=MicroServices.Statistics)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface QcDailyStatisticsClient {

    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileCreateDate, method = RequestMethod.GET)
    @ApiOperation(value = "按入库时间统计")
    Map<String, Map<String, Long>> search(
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate);




}
