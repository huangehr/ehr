package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @created by Sxy on 2016/08/01.
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsResourceIntegratedClient {

    @ApiOperation("综合查询档案数据列表树")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    Envelop getMetadataList(
            @RequestParam(value = "filters", required = false) String filters);

    @ApiOperation("综合查询档案数据检索")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    Envelop searchMetadataData(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "metaData", required = false) String metaData,
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "queryCondition",required = false) String queryCondition,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size);

    @ApiOperation("综合查询指标统计列表树")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    Envelop getQuotaList(
            @RequestParam(value = "filters", required = false) String filters);

    @ApiOperation("综合查询视图保存")
    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Envelop updateResource(
            @RequestParam(value = "dataJson") String dataJson);

    @ApiOperation("综合查询搜索条件更新")
    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Envelop updateResourceQuery(
            @RequestParam(value = "dataJson") String dataJson);

}
