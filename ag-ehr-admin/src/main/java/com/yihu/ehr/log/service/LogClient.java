package com.yihu.ehr.log.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.portal.MPortalSetting;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/17.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface LogClient {

    @RequestMapping(value = "/log/getBussinessLogs", method = RequestMethod.GET)
    @ApiOperation(value = "业务日志列表")
    ListResult getBussinessLogs(
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "caller", required = false) String caller,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    );

    @RequestMapping(value = "/log/getOperatorLogs", method = RequestMethod.GET)
    @ApiOperation(value = "操作日志列表")
    ListResult getOperatorLogs(
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "caller", required = false) String caller,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    );

}
