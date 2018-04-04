package com.yihu.ehr.log.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.portal.MPortalSetting;
import com.yihu.ehr.util.rest.Envelop;
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

    @RequestMapping(value = "/getBussinessLogById/{logId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取业务日志详情")
    ListResult getBussinessLogById(
            @RequestParam(value = "logId", required = false) String logId
    );

    @RequestMapping(value = "/getOperatorLogById/{logId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取操作日志详情")
    ListResult getOperatorLogById(
            @RequestParam(value = "logId", required = false) String logId
    );

    @RequestMapping(value = "/log/getBussinessListLogs", method = RequestMethod.GET)
    @ApiOperation(value = "业务日志列表,姓名模糊查询")
    ListResult getBussinessListLogs(
            @RequestParam(value = "patient", required = false) String patient,
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "caller", required = false) String caller,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    );

    @RequestMapping(value = "/log/getOperatorListLogs", method = RequestMethod.GET)
    @ApiOperation(value = "操作日志列表,姓名模糊查询")
    ListResult getOperatorListLogs(
            @RequestParam(value = "patient", required = false) String patient,
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "caller", required = false) String caller,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    );

    @RequestMapping(value = "/getOperatorLogByAppKey", method = RequestMethod.GET)
    @ApiOperation(value = "根据id进行MONGODB日志的查询")
    Envelop getOperatorLogByAppKey(
            @ApiParam(name = "appKey", value = "应用ID appkey就是appId", defaultValue = "")
            @RequestParam(value = "appKey", required = true) String appKey,
            @ApiParam(name = "responseFlag", value = "接口请求返回标识 1 成功 2 失败", defaultValue = "1")
            @RequestParam(value = "responseFlag", required = true) int responseFlag
    );
}
