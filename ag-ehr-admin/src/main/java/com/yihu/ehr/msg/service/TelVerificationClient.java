package com.yihu.ehr.msg.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(name= MicroServices.Organization)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TelVerificationClient {

    @RequestMapping(value = ServiceApi.TelVerification.TelVerification, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建手机验证信息", notes = "创建手机验证信息")
    Envelop createTelVerification(
            @ApiParam(name = "telNo", value = "", defaultValue = "")
            @RequestParam(value = "telNo", required = false) String telNo,
            @ApiParam(name = "appId", value = "", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId) throws Exception ;

    @RequestMapping(value = ServiceApi.TelVerification.TelVerification, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "验证手机验证信息", notes = "验证手机验证信息")
    Envelop validationTelVerification(
            @ApiParam(name = "telNo", value = "", defaultValue = "")
            @RequestParam(value = "telNo", required = false) String telNo,
            @ApiParam(name = "appId", value = "", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "verificationCode", value = "", defaultValue = "")
            @RequestParam(value = "verificationCode", required = false) String verificationCode) throws Exception ;

}
