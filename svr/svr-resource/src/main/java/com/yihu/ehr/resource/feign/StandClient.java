package com.yihu.ehr.resource.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @created 2017.05.16
 */
@ApiIgnore
@FeignClient(name = MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
public interface StandClient {

    @RequestMapping(value = ServiceApi.Standards.AdaptMeta, method = RequestMethod.GET)
    @ApiOperation(value = "获取ehr适配的数据元", notes = "获取ehr适配的数据元")
    public List<Map<String, Object>> getEhrAdapterMetadata(@RequestParam(value = "version") String version);

    @RequestMapping(value = ServiceApi.Standards.AdaptDict, method = RequestMethod.GET)
    @ApiOperation(value = "获取ehr适配的字典", notes = "获取ehr适配的字典")
    public List<Map<String, Object>> getEhrAdapterDict(@RequestParam(value = "version") String version);
}
