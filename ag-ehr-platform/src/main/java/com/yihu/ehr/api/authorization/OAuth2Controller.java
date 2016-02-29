package com.yihu.ehr.api.authorization;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OAuth2 认证控制器。此控制器基于用户名/密码认证，即Basic Authorization，需要在HTTP中提供用户名与密码。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/authorizations")
@Api(protocols = "https", value = "authorizations", description = "OAuth2授权服务")
public class OAuth2Controller {

    @ApiOperation(value = "获取用户所有应用授权", response = String.class)
    @RequestMapping(value = "", produces = "application/json", method = RequestMethod.GET)
    public List<String> getAuthorizations() {
        return null;
    }

    @ApiOperation(value = "获取单个授权", response = String.class)
    @RequestMapping(value = "/{token_id}", produces = "application/json", method = RequestMethod.GET)
    public List<String> getAuthorization(@ApiParam(value = "token_id")
                                         @PathVariable("token_id") long tokenId) {
        return null;
    }

    @ApiOperation(value = "更新授权", response = String.class)
    @RequestMapping(value = "/{token_id}", produces = "application/json", method = RequestMethod.PUT)
    public List<String> updateAuthorization(@ApiParam(value = "token_id")
                                            @PathVariable(value = "token_id") long id,
                                            @ApiParam(value = "json")
                                            @RequestParam("json") String json) {
        return null;
    }

    @ApiOperation(value = "删除授权", response = String.class)
    @RequestMapping(value = "/{token_id}", produces = "application/json", method = RequestMethod.DELETE)
    public void deleteAuthorization(@ApiParam(value = "token_id")
                                    @PathVariable(value = "token_id") long id) {

    }

    @ApiOperation(value = "为用户的多个应用创建授权", response = String.class)
    @RequestMapping(value = "", produces = "application/json", method = RequestMethod.POST)
    public List<String> createAuthorizations(@ApiParam(value = "json")
                                             @RequestParam(value = "json") String json) {
        return null;
    }

    @ApiOperation(value = "为指定应用创建授权", response = String.class)
    @RequestMapping(value = "/clients/{client_id}", produces = "application/json", method = RequestMethod.PUT)
    public List<String> createAuthorization(@ApiParam(value = "json")
                                            @RequestParam("json") String json) {
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
