package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.Envelop;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author hzp
 * @version 1.0
 * @created 2016.06.12 14:58
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Resource)
public interface XTransformClient {

    @RequestMapping(value = "/rs/transform/stdTransformList", method = RequestMethod.POST)
    List<Map<String, Object>> stdTransformList(@RequestParam(value = "resource", required = true) String resource,
                                               @RequestParam(value = "version", required = true) String version);

    @RequestMapping(value = "/rs/transform/stdTransform", method = RequestMethod.POST)
    Map<String, Object> stdTransform(
            @RequestParam(value = "resource", required = true) String resource,
            @RequestParam(value = "version", required = true) String version);
}
