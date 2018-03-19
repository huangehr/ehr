package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MRsSystemDictionary;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
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
public interface RsResourceStatisticsClient {

    @RequestMapping(value = ServiceApi.Resources.StatisticsGetDoctorsGroupByTown, method = RequestMethod.GET)
    @ApiOperation(value = "获取各行政区划总卫生人员", notes = "获取各行政区划总卫生人员")
    Envelop statisticsGetDoctorsGroupByTown();


    @ApiOperation(value = "获取特殊机构指标执行结果分页")
    @RequestMapping(value = ServiceApi.TJ.TjGetOrgHealthCategoryQuotaResult, method = RequestMethod.GET)
    Envelop getOrgHealthCategoryQuotaResult(
            @ApiParam(name = "code", value = "指标任务code", required = true)
            @RequestParam(value = "code" , required = true) String code,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "需要统计不同维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension );


    @ApiOperation(value = "获取指标统计报表 二维表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReportTwoDimensionalTable, method = RequestMethod.GET)
    List<Map<String, Object>> getQuotaReportTwoDimensionalTable(
            @ApiParam(name = "quotaCodeStr", value = "指标Code,多个用,拼接", required = true)
            @RequestParam(value = "quotaCodeStr" , required = true) String quotaCodeStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension);

    @RequestMapping(value = ServiceApi.TJ.GetArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案总数")
    Envelop getArchiveCount();

    @RequestMapping(value = ServiceApi.TJ.GetArchiveManCount, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案的建档人数数量")
    Envelop getArchiveManCount();
}
