package com.yihu.ehr.pack.controller;

import com.yihu.ehr.constants.ApiVersion;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证网关。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:55
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/users")
public class UserController {
    public String authorize(String userName, String password){
        return "";
    }
}
