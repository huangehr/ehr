package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsResources;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourcesClient {

    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsResources createResource(
            @RequestBody String resource);

    @ApiOperation("更新资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsResources updateResources(
            @RequestBody String resource);

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.DELETE)
    boolean deleteResources(
            @PathVariable(value = "id") String id);

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.DELETE)
    boolean deleteResourcesBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Resources.Resource,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源")
    public MRsResources getResourceById(
            @PathVariable(value="id") String id);

    @ApiOperation("资源查询")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.GET)
    ResponseEntity<List<MRsResources>> queryResources(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

}
