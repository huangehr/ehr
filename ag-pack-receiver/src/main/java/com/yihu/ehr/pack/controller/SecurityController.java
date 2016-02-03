package com.yihu.ehr.pack.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 安全控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersionPrefix.Version1_0 + "/security")
public class SecurityController {

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
}
