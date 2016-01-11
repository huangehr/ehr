package com.yihu.ehr.user.user.controller;

import com.yihu.ha.constrant.ApiVersionPrefix;
import com.yihu.ha.constrant.ErrorCode;
import com.yihu.ha.util.RestEcho;
import com.yihu.ha.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.14 17:57
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/user")
@Api(protocols = "https", value = "User", description = "用户管理接口", tags = {"用户"})
public class UserRestController extends BaseRestController {

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "禁用用户", response = RestEcho.class, produces = "application/json", notes = "禁用指定用户的信息")
    public RestEcho invalidUser(
            @ApiParam(required = true, name = "id", value = "用户id")
            @PathVariable(value = "id") String id
    ) {
        return (RestEcho)failed(ErrorCode.InvalidUser);
    }
}
