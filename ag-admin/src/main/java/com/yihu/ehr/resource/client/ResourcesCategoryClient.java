package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsCategory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourcesCategoryClient {

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.POST)
    @ApiOperation("资源类别创建")
    MRsCategory createRsCategory(
            @RequestParam(value = "resourceCategory") String resourceCategory);

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.PUT)
    @ApiOperation("资源类别更新")
    MRsCategory updateRsCategory(
            @RequestParam(value = "resourceCategory") String resourceCategory);

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    boolean deleteResourceCategory(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    Page<MRsCategory> getRsCategories(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size
    );

}
