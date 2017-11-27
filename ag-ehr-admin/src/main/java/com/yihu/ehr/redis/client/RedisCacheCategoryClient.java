package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 缓存分类 Client
 *
 * @author 张进军
 * @date 2017/11/24 16:08
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisCacheCategoryClient {

    @ApiOperation("根据ID获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation(value = "根据条件获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("新增缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson);

    @ApiOperation("更新缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("删除缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证缓存分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存分类名称", required = true)
            @RequestParam(value = "name") String name);

    @ApiOperation("验证缓存分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存分类编码", required = true)
            @RequestParam(value = "code") String code);

}
