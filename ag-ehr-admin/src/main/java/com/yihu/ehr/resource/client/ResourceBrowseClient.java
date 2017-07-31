package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wq on 2016/6/3.
 */

@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceBrowseClient {



    /**
     *资源数据源结构
     */
    @ApiOperation("资源数据源结构")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewMetadata, method = RequestMethod.GET)
    public String getResourceMetadata(@RequestParam(value = "resourcesCode") String resourcesCode);


    @ApiOperation("资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewData, method = RequestMethod.GET)
    Envelop getResourceData(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "orgCode") String orgCode,
            @RequestParam(value = "queryCondition",required = false) String queryCondition,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size);

    /**
    @ApiOperation("资源浏览详细信息")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewData, method = RequestMethod.GET)
    Envelop getResourceDataSub(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "rowKey") String rowKey);
    */
}
