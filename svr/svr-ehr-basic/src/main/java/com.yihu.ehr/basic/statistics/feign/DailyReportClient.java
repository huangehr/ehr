package com.yihu.ehr.basic.statistics.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

@ApiIgnore
@FeignClient(value = MicroServices.Analyzer)
@RequestMapping(ApiVersion.Version1_0)
public interface DailyReportClient {

    @RequestMapping(value = ServiceApi.PackageAnalyzer.List, method = RequestMethod.POST)
    Envelop list(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter);
}
