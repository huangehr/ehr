package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @version 1.0
 * @created 2016.06.12 14:58
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Resource)
public interface XTransformClient {

    @RequestMapping(value = "/rs/transform/stdTransformList", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Map<String, Object>> stdTransformList(
            @RequestBody String stdTransformDtoJson);

    @RequestMapping(value = "/rs/transform/stdTransform", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Map<String, Object> stdTransform(
            @RequestBody String stdTransformDtoJson);

    @RequestMapping(value = "/rs/transform/stdMasterTransform", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Map<String, Object> stdMasterTransform(
            @RequestBody String stdTransformDtoJson);
}
