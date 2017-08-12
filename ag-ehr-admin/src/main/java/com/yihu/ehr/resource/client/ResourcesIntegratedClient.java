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
public interface ResourcesIntegratedClient {

    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据列表树")
    List<Map<String, Object>> getMetadataList(
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据检索")
    List<Map<String, Object>> searchMetadataData(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "metaData", required = false) String metaData,
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "queryCondition",required = false) String queryCondition,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size);

    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    @ApiOperation("综合查询指标统计列表树")
    List<Map<String, Object>> getQuotaList(
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    @ApiOperation("综合查询视图保存")
    Envelop updateResource(
            @RequestParam(value = "dataJson") String dataJson);

    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    @ApiOperation("综合查询搜索条件更新")
    Envelop updateResourceQuery(
            @RequestParam(value = "dataJson") String dataJson);

}
