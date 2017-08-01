package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author Sxy
 * @created 2016.08.01 17:27
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourcesCustomizeClient {

    @RequestMapping(value = ServiceApi.Resources.NoPageCustomizeList, method = RequestMethod.GET)
    @ApiOperation("获取资源列表树")
    List<Map<String, Object>> getCustomizeList(
            @RequestParam(value = "filters", required = false) String filters);
}
