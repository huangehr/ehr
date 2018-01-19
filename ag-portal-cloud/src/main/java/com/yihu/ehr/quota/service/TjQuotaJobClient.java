package com.yihu.ehr.quota.service;

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
 * Created by Administrator on 2017/6/9.
 */
@FeignClient(name= MicroServices.Quota)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaJobClient {

    @RequestMapping(value = ServiceApi.TJ.TjGetQuotaResult, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标执行结果")
    Envelop getQuotaResult(
            @RequestParam(value = "id", required = true) int id,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "pageNo", required = false) int pageNo,
            @RequestParam(value = "pageSize", required = false) int pageSize
    );


    @RequestMapping(value = ServiceApi.TJ.GetQuotaReport, method = RequestMethod.GET)
    @ApiOperation(value = "getQuotaReport")
    Envelop getQuotaReport(
            @RequestParam(value = "id", required = true) int id,
            @RequestParam(value = "filters", required = false) String filters
    );

}
