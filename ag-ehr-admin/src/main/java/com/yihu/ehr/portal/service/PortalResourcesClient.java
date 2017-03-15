package com.yihu.ehr.portal.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MPortalResources;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/17.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalResourcesClient {

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源列表", notes = "根据查询条件获取资源列表在前端表格展示")
    ResponseEntity<List<MPortalResources>> searchPortalResources(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息", notes = "资源信息")
    MPortalResources getPortalResources(@PathVariable(value = "portalResources_id") Long portalResourcesId);

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建资源", notes = "重新绑定资源信息")
    MPortalResources createPortalResources(
            @ApiParam(name = "portalResources_json_data", value = "", defaultValue = "")
            @RequestBody String portalResourcesJsonData);

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改资源", notes = "重新绑定资源信息")
    MPortalResources updatePortalResources(@ApiParam(name = "portalResources_json_data", value = "", defaultValue = "")
                                     @RequestBody String portalResourcesJsonData);

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源", notes = "根据资源id删除")
    boolean deletePortalResources(@PathVariable(value = "portalResources_id") String portalResourcesId);

}
