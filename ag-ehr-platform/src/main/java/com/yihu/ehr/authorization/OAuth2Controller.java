package com.yihu.ehr.authorization;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OAuth2 认证控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/authorizations")
@Api(protocols = "https", value = "authorizations", description = "OAuth2授权服务")
public class OAuth2Controller {

    @ApiOperation(value = "", response = String.class)
    @RequestMapping(value = "/authorizations", produces = "application/text", method = RequestMethod.POST)
    public List<String> createAuthorization(@RequestParam(value = "json")
                                            String json) {
        return null;
    }

    @ApiOperation(value = "/{client_id}", response = String.class)
    @RequestMapping(value = "/{client_id}", produces = "application/text", method = RequestMethod.GET)
    public List<String> getAuthorizations(@PathVariable("client_id")
                                          String clientId) {
        return null;
    }

    @ApiOperation(value = "", response = String.class)
    @RequestMapping(value = "/{client_id}", produces = "application/text", method = RequestMethod.GET)
    public List<String> getAuthorization(@PathVariable("client_id")
                                         String clientId) {
        return null;
    }

    /*
    @ApiOperation(value = "/user_key", response = String.class)
    @RequestMapping(value = "/user_key", produces = "application/text", method = RequestMethod.GET)
    public String getUserKey(){
        return null;
    }

    @ApiOperation(value = "/token", response = String.class)
    @RequestMapping(value = "/token", produces = "application/text", method = RequestMethod.GET)
    public String getAppToken(){
        return null;
    }

    @RequestMapping(value = "/{client_id}", method = RequestMethod.POST)
    public String getToken(@PathVariable String appId, @RequestParam String secret){
        return "";
    }*/
}
