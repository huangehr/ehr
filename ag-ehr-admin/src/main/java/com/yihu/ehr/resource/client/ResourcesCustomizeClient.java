package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
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

    @RequestMapping(value = ServiceApi.Resources.CustomizeList, method = RequestMethod.GET)
    @ApiOperation("获取自定义资源列表树")
    List<Map<String, Object>> getCustomizeList(
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ServiceApi.Resources.CustomizeData, method = RequestMethod.GET)
    @ApiOperation("获取自定义资源数据")
    List<Map<String, Object>> getCustomizeData(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "metaData", required = false) String metaData,
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "queryCondition",required = false) String queryCondition,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size);

    @RequestMapping(value = ServiceApi.Resources.CustomizeUpdate, method = RequestMethod.POST)
    @ApiOperation("自定义资源视图保存")
    Envelop customizeUpdate(
            @RequestParam(value = "dataJson") String dataJson);


}
