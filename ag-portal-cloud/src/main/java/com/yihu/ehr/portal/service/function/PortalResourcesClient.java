package com.yihu.ehr.portal.service.function;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MPortalResources;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by ysj on 2017年2月20日11:30:03.
 */
@FeignClient(name=MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalResourcesClient {

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有资源信息数据", notes = "查询的数据在前端表格展示")
    ResponseEntity<List<MPortalResources>> getAllPortalResources();

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息列表", notes = "根据查询条件获取资源信息列表在前端表格展示")
    ResponseEntity<List<MPortalResources>> searchPortalResources(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息信息", notes = "资源信息信息")
    MPortalResources getPortalResources(@PathVariable(value = "portalResources_id") Long portalResourcesId);
}
