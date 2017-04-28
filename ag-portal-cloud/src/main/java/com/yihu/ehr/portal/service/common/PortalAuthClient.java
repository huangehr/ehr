package com.yihu.ehr.portal.service.common;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ObjectResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by yeshijie on 2017/2/21.
 */
@FeignClient(name= MicroServices.Authentication)
@ApiIgnore
public interface PortalAuthClient {

    @RequestMapping(value = ServiceApi.Authentication.ValidToken, method = RequestMethod.POST)
    @ApiOperation(value = "验证Token", notes = "验证Token")
    ObjectResult validToken(@RequestParam(value = "clientId") String clientId,
                                   @RequestParam(value = "accessToken") String accessToken) throws Exception;
}
