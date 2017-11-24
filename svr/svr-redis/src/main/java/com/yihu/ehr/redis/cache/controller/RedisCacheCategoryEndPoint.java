package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheCategory;
import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import com.yihu.ehr.redis.cache.service.RedisCacheCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存分类 接口
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存分类接口", tags = {"缓存服务管理--缓存分类接口"})
public class RedisCacheCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheCategoryService redisCacheCategoryService;

    @ApiOperation("根据ID获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.GetById, method = RequestMethod.GET)
    public MRedisCacheCategory getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(redisCacheCategoryService.getById(id), MRedisCacheCategory.class);
    }

    @ApiOperation(value = "根据条件获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Search, method = RequestMethod.GET)
    List<MRedisCacheCategory> search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RedisCacheCategory> redisMqChannels = redisCacheCategoryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, redisCacheCategoryService.getCount(filters), page, size);
        return (List<MRedisCacheCategory>) convertToModels(redisMqChannels, new ArrayList<MRedisCacheCategory>(), MRedisCacheCategory.class, fields);
    }

    @ApiOperation("新增缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.POST)
    public MRedisCacheCategory add(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisCacheCategory newRedisCacheCategory = toEntity(entityJson, RedisCacheCategory.class);
        newRedisCacheCategory = redisCacheCategoryService.save(newRedisCacheCategory);
        return convertToModel(newRedisCacheCategory, MRedisCacheCategory.class);
    }

    @ApiOperation("更新缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.PUT)
    public MRedisCacheCategory update(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisCacheCategory updateRedisCacheCategory = toEntity(entityJson, RedisCacheCategory.class);
        updateRedisCacheCategory = redisCacheCategoryService.save(updateRedisCacheCategory);
        return convertToModel(updateRedisCacheCategory, MRedisCacheCategory.class);
    }

    @ApiOperation("删除缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        redisCacheCategoryService.delete(id);
    }

    @ApiOperation("验证缓存分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueName, method = RequestMethod.GET)
    public boolean isUniqueName(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存分类名称", required = true)
            @RequestParam(value = "name") String name) throws Exception {
        return redisCacheCategoryService.isUniqueName(id, name);
    }

    @ApiOperation("验证缓存分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueCode, method = RequestMethod.GET)
    public boolean isUniqueCode(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存分类编码", required = true)
            @RequestParam(value = "code") String code) throws Exception {
        return redisCacheCategoryService.isUniqueCode(id, code);
    }

}
