package com.yihu.ehr.authorization;

import com.yihu.ehr.constants.ApiVersionPrefix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 认证控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:24
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/authorizations")
public class OAuth2Controller {
}
