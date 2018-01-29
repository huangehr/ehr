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

import java.util.List;
import java.util.Map;

/**
 *
 * Created by wq on 2016/6/3.
 */

@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceBrowseClient {



    /**
     *资源数据源结构
     */
    @ApiOperation("档案资源数据元结构")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewMetadata, method = RequestMethod.GET)
    String getResourceMetadata(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "roleId") String roleId);


    @ApiOperation("档案资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewData, method = RequestMethod.GET)
    Envelop getResourceData(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "roleId") String roleId,
            @RequestParam(value = "orgCode") String orgCode,
            @RequestParam(value = "areaCode") String areaCode,
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

    @ApiOperation("档案资源浏览细表数据")
    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseResourceSubData, method = RequestMethod.GET)
    Envelop findSubDateByRowKey(
            @RequestParam(value = "rowKey") String rowKey,
            @RequestParam(value = "version") String version);
}
