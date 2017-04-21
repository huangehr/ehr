package com.yihu.ehr.portal.service.common;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by yeshijie on 2017/2/21.
 */
@FeignClient(name= MicroServices.Authentication)
@ApiIgnore
public interface PortalAuthClient {

    @RequestMapping(value = ServiceApi.Authentication.AccessToken)
    @ApiOperation(value = "获取token", notes = "获取token")
    String accessToken(@RequestBody Map<String, String> parameters) throws Exception;


    @RequestMapping(value = ServiceApi.Authentication.ValidToken, method = RequestMethod.GET)
    @ApiOperation(value = "验证Token", notes = "验证Token")
    ObjectResult validToken(@RequestParam(value = "client_id") String clientId,
                                   @RequestParam(value = "access_token") String accessToken) throws Exception;
}
