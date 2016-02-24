package com.yihu.ehr.authorization;

import com.yihu.ehr.constants.ApiVersion;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic认证控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:25
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/basic-authorizations")
public class BasicAuthController {
}
