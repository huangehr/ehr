package com.yihu.ehr.resource.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
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
public interface ResourcesCategoryClient {

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("资源类别创建")
    MRsCategory createRsCategory(
            @RequestBody String resourceCategory);

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("资源类别更新")
    MRsCategory updateRsCategory(
            @RequestBody String resourceCategory);

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    Envelop deleteResourceCategory(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.Category,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源类别")
    public MRsCategory getRsCategoryById(
            @PathVariable(value="id") String id);

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    ResponseEntity<List<MRsCategory>> getRsCategories(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size
    );

    @RequestMapping(value = ServiceApi.Resources.NoPageCategories,method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    List<MRsCategory> getAllCategories(
            @RequestParam(value="filters",required = false)String filters);

    @RequestMapping(value = ServiceApi.Resources.CategoryByPid,method = RequestMethod.GET)
    @ApiOperation("根据pid获取资源类别列表")
    List<MRsCategory> getRsCategoryByPid(
            @RequestParam(value="pid",required = false) String pid);

    @RequestMapping(value = ServiceApi.Resources.CategoryExitSelfAndChild, method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    List<MRsCategory> getCateTypeExcludeSelfAndChildren(@ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id);
}
