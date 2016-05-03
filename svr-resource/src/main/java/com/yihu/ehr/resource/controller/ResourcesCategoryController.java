package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hzp on 2016/4/19.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/category")
@Api(value = "rsCategory", description = "资源分类服务接口")
public class ResourcesCategoryController {
    /*@Autowired
    private IResourcesCategoryService resourcesCategoryService;

    @RequestMapping(value = "/addResourcesCategory", method = RequestMethod.POST)
    @ApiOperation(value = "新增资源分类")
    public Result addResourcesCategory(
            @ApiParam(name = "category", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"pid\": \"\", \"description\": \"\"}")
            @RequestParam(value = "category", required = true) String categoryJson) throws Exception {
            RsCategory obj = (RsCategory)JSONObject.toBean(JSONObject.fromObject(categoryJson),RsCategory.class);
            return resourcesCategoryService.addResourcesCategory(obj);
    }

    @RequestMapping(value = "/editResourcesCategory", method = RequestMethod.POST)
    @ApiOperation(value = "修改资源分类")
    public Result editResourcesCategory(
            @ApiParam(name = "category", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"id\": \"\",\"name\": \"\", \"pid\": \"\", \"description\": \"\"}")
            @RequestParam(value = "category", required = true) String categoryJson) {
        try {
            RsCategory obj = (RsCategory)JSONObject.toBean(JSONObject.fromObject(categoryJson),RsCategory.class);
            return resourcesCategoryService.editResourcesCategory(obj);
        }
        catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/deleteResourcesCategory", method = RequestMethod.POST)
    @ApiOperation(value = "删除资源分类")
    public Result deleteResourcesCategory(
            @ApiParam(name = "id", value = "主键") @RequestParam(value = "id", required = true) String id) {
        try {
            return resourcesCategoryService.deleteResourcesCategory(id);
        }
        catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取资源分类")
    @RequestMapping(value = "/queryResourcesCategory", method = RequestMethod.GET)
    public Result queryResourcesCategory(@ApiParam("id") @RequestParam(value = "id", required = false) String id,@ApiParam("name") @RequestParam(value = "name", required = false) String name,@ApiParam("pid") @RequestParam(value = "pid", required = false) String pid) {
        try {
            return resourcesCategoryService.queryResourcesCategory(id,name,pid);
        }
        catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
*/
}
