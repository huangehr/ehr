package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.Envelop;
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
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/query")
@ApiIgnore
public interface ResourceBrowseClient {



    /**
     *资源数据源结构
     */
    @ApiOperation("资源数据源结构")
    @RequestMapping(value = "/getResourceMetadata", method = RequestMethod.GET)
    public String getResourceMetadata(
            @ApiParam("resourcesCode")
            @RequestParam(value = "resourcesCode", required = true) String resourcesCode);


    @ApiOperation("资源浏览")
    @RequestMapping(value = "/getResourceData", method = RequestMethod.GET)
    Envelop getResourceData(
            @ApiParam("resourcesCode")
            @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
            @ApiParam("queryCondition")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam("page")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size")
            @RequestParam(value = "size", required = false) Integer size);


}
