package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/12/14.
 */
@FeignClient(name = MicroServices.Quota)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface QuotaStatisticsClient {


    @ApiOperation(value = "获取指标统计报表 二维表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReportTwoDimensionalTable, method = RequestMethod.GET)
    List<Map<String, Object>> getQuotaReportTwoDimensionalTable(
            @ApiParam(name = "quotaCodeStr", value = "指标Code,多个用,拼接", required = true)
            @RequestParam(value = "quotaCodeStr", required = true) String quotaCodeStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top);

}
