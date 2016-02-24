package com.yihu.ehr.app;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.24 17:25
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/applications")
@Api(protocols = "https", value = "applications", description = "应用管理服务")
public class AppController {
    @ApiOperation(value = "检查应用授权", response = String.class)
    @RequestMapping(value = "/{client_id}/tokens/{access_token}", produces = "application/json", method = RequestMethod.GET)
    public String checkClientAuthorization(@ApiParam(value = "client_id")
                                                 @PathVariable("client_id") String clientId,
                                                 @ApiParam(value = "access_token")
                                                 @PathVariable("access_token") String accessToken) {
        return null;
    }

    @ApiOperation(value = "重置应用授权", response = String.class)
    @RequestMapping(value = "/{client_id}/tokens/{access_token}", produces = "application/json", method = RequestMethod.POST)
    public String restClientAuthorization(@ApiParam(value = "client_id")
                                                @PathVariable("client_id") String clientId,
                                                @ApiParam(value = "access_token")
                                                @PathVariable("access_token") String accessToken) {
        return null;
    }

    @ApiOperation(value = "删除应用授权", response = String.class)
    @RequestMapping(value = "/{client_id}/tokens/{access_token}", produces = "application/json", method = RequestMethod.DELETE)
    public void revokeClientAuthorization(@ApiParam(value = "client_id")
                                                @PathVariable("client_id") String clientId,
                                                @ApiParam(value = "access_token")
                                                @PathVariable("access_token") String accessToken) {
    }
}
