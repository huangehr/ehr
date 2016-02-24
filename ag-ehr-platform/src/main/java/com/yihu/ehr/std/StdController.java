package com.yihu.ehr.std;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 18:05
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/standards")
@Api(protocols = "https", value = "standards", description = "标准服务")
public class StdController {
}
