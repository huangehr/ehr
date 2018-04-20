package com.yihu.ehr.basic.statistics.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApiIgnore
@FeignClient(value = MicroServices.Analyzer)
@RequestMapping(ApiVersion.Version1_0)
public interface DailyReportClient {

    @RequestMapping(value = ServiceApi.PackageAnalyzer.DailyReport, method = RequestMethod.POST)
    Envelop dailyReport(
            @ApiParam(name = "report", value = "日报json对象")
            @RequestParam(value = "report", required = true) String report);

    @RequestMapping(value = ServiceApi.PackageAnalyzer.List, method = RequestMethod.POST)
    Envelop list(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter);

    @ApiOperation(value = "根据sql查询")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.FindBySql, method = RequestMethod.POST)
    List<Map<String,Object>> findBySql(
            @ApiParam(name = "field", value = "字段列表", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "sql", value = "sql", required = true)
            @RequestParam(value = "sql") String sql);
}
