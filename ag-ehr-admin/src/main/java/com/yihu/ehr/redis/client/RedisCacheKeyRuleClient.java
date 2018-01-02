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
 * 缓存Key规则 Client
 *
 * @author 张进军
 * @date 2017/11/27 16:21
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisCacheKeyRuleClient {

    @ApiOperation("根据ID获取缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation(value = "根据条件获取缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Search, method = RequestMethod.GET)
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

    @ApiOperation("新增缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "缓存Key规则JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("更新缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "缓存Key规则JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("删除缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证缓存Key规则名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存Key规则名称", required = true)
            @RequestParam(value = "name") String name);

    @ApiOperation("验证缓存Key规则编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存Key规则编码", required = true)
            @RequestParam(value = "code") String code);

    @ApiOperation("验证类似的缓存Key规则表达式是否已经存在")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueExpression, method = RequestMethod.GET)
    public Envelop isUniqueExpression(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "categoryCode", value = "缓存分类编码", required = true)
            @RequestParam(value = "categoryCode") String categoryCode,
            @ApiParam(name = "expression", value = "缓存Key规则表达式", required = true)
            @RequestParam(value = "expression") String expression);

}
