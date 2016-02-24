package com.yihu.ehr.user;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
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
@Api(protocols = "https", value = "users", description = "用户服务")
public class UserController {
    public String authorize(String userName, String password){
        return "";
    }
}
