package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.tj.MTjQuotaLog;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaLogClient {

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaLogList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计日志列表")
    ResponseEntity<List<MTjQuotaLog>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "quotaCode", required = false) String quotaCode,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaLogRecentRecord, method = RequestMethod.GET)
    @ApiOperation(value = "获取最近日志列表")
    MTjQuotaLog getRecentRecord(
            @RequestParam(value = "quotaCode", required = false) String quotaCode,
            @RequestParam(value = "endTime", required = false) Date endTime
    );




}
