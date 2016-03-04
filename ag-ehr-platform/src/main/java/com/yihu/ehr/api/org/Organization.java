package com.yihu.ehr.api.org;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.04 9:03
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/organizations")
@Api(protocols = "https", value = "organizations", description = "组织机构服务")
public class Organization {
}
