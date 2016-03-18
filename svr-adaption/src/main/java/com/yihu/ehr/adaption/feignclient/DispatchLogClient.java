package com.yihu.ehr.adaption.feignclient;


import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MDispatchLog;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;


/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
@ApiIgnore
@FeignClient(name = MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0)
public interface DispatchLogClient {


    @RequestMapping(value = RestApi.Standards.DispatchLogs, method = RequestMethod.GET)
    @ApiOperation(value = "获取日志信息")
    MDispatchLog getLog(
            @ApiParam(required = true, name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode);


    @RequestMapping(value = RestApi.Standards.DispatchLogs, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除日志信息")
    boolean deleteLog(
            @ApiParam(required = true, name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode);

    @RequestMapping(value = RestApi.Standards.DispatchLogs, method = RequestMethod.POST)
    @ApiOperation(value = "新增日志信息")
    MDispatchLog saveLog(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model);

}
