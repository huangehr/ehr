package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.11 9:02
 */
@ApiIgnore
@FeignClient(MicroServices.Standard)
public interface XCDAVersionClient {
}
