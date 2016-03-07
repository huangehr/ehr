package com.yihu.ehr.api.authorization;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器。此控制器基于用户名/密码认证，即Basic Authorization，需要在HTTPS中提供用户名与密码。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/authorizations")
@Api(protocols = "https", value = "authorizations", description = "认证与授权服务")
public class AuthorizationsEndPoint {

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

    @ApiOperation(value = "为指定应用创建授权，若存在返回已有授权", response = String.class)
    @RequestMapping(value = "/clients/{client_id}", produces = "application/json", method = RequestMethod.PUT)
    public List<String> createClientAuthorization(@ApiParam("client_id")
                                                  @PathVariable("client_id")
                                                  String clientId,
                                                  @ApiParam(value = "info")
                                                  @RequestParam("info") String info) {
        return null;
    }

    //------ 用户Token ------

    @ApiOperation("获取用户Token列表")
    @RequestMapping(value = "/users/{user_name}/tokens", method = RequestMethod.GET)
    public List<String> getTokens(@ApiParam("user_name")
                                  @PathVariable("user_name")
                                  String userName) {
        return null;
    }

    @ApiOperation("获取用户单个Token")
    @RequestMapping(value = "/users/{user_name}/tokens/{id}", method = RequestMethod.GET)
    public String getToken(@ApiParam("id")
                           @PathVariable("id")
                           String id) {
        return "";
    }

    @ApiOperation(value = "为指定用户创建授权，若存在返回已有授权", response = String.class)
    @RequestMapping(value = "/users/{user_name}", produces = "application/json", method = RequestMethod.PUT)
    public List<String> createUserAuthorization(@ApiParam("user_name")
                                                @PathVariable("user_name")
                                                String userName,
                                                @ApiParam(value = "info")
                                                @RequestParam("info") String info) {
        return null;
    }
}
