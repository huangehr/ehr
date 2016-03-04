package com.yihu.ehr.adaption.feignclient;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MDispatchLog;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
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
@FeignClient(MicroServices.StandardMgr + "/std/dispatch")
@RequestMapping(ApiVersion.Version1_0)
public interface DispatchLogClient {

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    @ApiOperation(value = "获取日志信息")
    public MDispatchLog getLog(
            @ApiParam(required = true, name = "versionCode", value = "版本号")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode", required = true) String orgCode) throws Exception;
}
