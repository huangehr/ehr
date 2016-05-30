package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsCategory;
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
    boolean deleteResourceCategory(
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

}
