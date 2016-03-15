package com.yihu.ehr.adaption.feignclient;


import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.model.standard.MDispatchLog;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
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
@FeignClient(name = MicroServiceName.Standard,url = MicroServiceIpAddressStr.Standard+ MicroServicePort.Standard)
@RequestMapping(ApiVersion.Version1_0 + "/std")
public interface StdVersionClient {

    @RequestMapping(value = "/version/{version}", method = RequestMethod.GET)
    @ApiOperation(value = "获取版本信息")
    public MCDAVersion getVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version ) throws Exception ;
}
