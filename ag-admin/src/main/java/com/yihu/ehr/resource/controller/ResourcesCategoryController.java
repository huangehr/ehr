package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.client.ResourcesCategoryClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceCategory", description = "资源分类服务接口")
public class ResourcesCategoryController extends BaseController {

    @Autowired
    private ResourcesCategoryClient resourcesCategoryClient;

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.POST)
    @ApiOperation("资源类别创建")
    public MRsCategory createRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(value = "resourceCategory") String resourceCategory) throws Exception {
        return resourcesCategoryClient.createRsCategory(resourceCategory);
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.PUT)
    @ApiOperation("资源类别更新")
    public MRsCategory updateRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"id\":\"string\",\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(value = "resourceCategory") String resourceCategory) throws Exception {
        return resourcesCategoryClient.updateRsCategory(resourceCategory);
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    public boolean deleteResourceCategory(
            @ApiParam(name = "id", value = "资源类别ID", defaultValue = "string")
            @PathVariable(value = "id") String id) throws Exception {
        return resourcesCategoryClient.deleteResourceCategory(id);
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public Page<MRsCategory> getRsCategories(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        return resourcesCategoryClient.getRsCategories(fields,filters,sorts,page,size);
    }

}
