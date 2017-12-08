package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.redis.client.RedisCacheCategoryClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 缓存分类 Controller
 *
 * @author 张进军
 * @date 2017/11/24 16:08
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "缓存分类接口", tags = {"缓存服务管理--缓存分类接口"})
public class RedisCacheCategoryController extends BaseController {

    @Autowired
    private RedisCacheCategoryClient redisCacheCategoryClient;

    @ApiOperation("根据ID获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        return redisCacheCategoryClient.getById(id);
    }

    @ApiOperation(value = "根据条件获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) int size) {
        return redisCacheCategoryClient.search(fields, filters, sorts, page, size);
    }

    @ApiOperation(value = "根据条件获取缓存分类（不分页）")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.SearchNoPage, method = RequestMethod.GET)
    public Envelop searchNoPage(
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts) {
        return redisCacheCategoryClient.searchNoPage(filters, sorts);
    }

    @ApiOperation("新增缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return redisCacheCategoryClient.add(entityJson);
    }

    @ApiOperation("更新缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return redisCacheCategoryClient.update(entityJson);
    }

    @ApiOperation("删除缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id) {
        return redisCacheCategoryClient.delete(id);
    }

    @ApiOperation("验证缓存分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存分类名称", required = true)
            @RequestParam(value = "name") String name) {
        return redisCacheCategoryClient.isUniqueName(id, name);
    }

    @ApiOperation("验证缓存分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存分类编码", required = true)
            @RequestParam(value = "code") String code) {
        return redisCacheCategoryClient.isUniqueCode(id, code);
    }

}
