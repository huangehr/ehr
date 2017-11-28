package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.redis.client.RedisCacheKeyRuleClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 缓存Key规则 Controller
 *
 * @author 张进军
 * @date 2017/11/27 16:21
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "缓存Key规则接口", tags = {"缓存服务管理--缓存Key规则接口"})
public class RedisCacheKeyRuleController extends BaseController {

    @Autowired
    private RedisCacheKeyRuleClient redisCacheKeyRuleClient;

    @ApiOperation("根据ID获取缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        return redisCacheKeyRuleClient.getById(id);
    }

    @ApiOperation(value = "根据条件获取缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Search, method = RequestMethod.GET)
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
        return redisCacheKeyRuleClient.search(fields, filters, sorts, page, size);
    }

    @ApiOperation("新增缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "缓存Key规则JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return redisCacheKeyRuleClient.add(entityJson);
    }

    @ApiOperation("更新缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "缓存Key规则JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return redisCacheKeyRuleClient.update(entityJson);
    }

    @ApiOperation("删除缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id) {
        return redisCacheKeyRuleClient.delete(id);
    }

    @ApiOperation("验证缓存Key规则名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存Key规则名称", required = true)
            @RequestParam(value = "name") String name) {
        return redisCacheKeyRuleClient.isUniqueName(id, name);
    }

    @ApiOperation("验证缓存Key规则编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存Key规则编码", required = true)
            @RequestParam(value = "code") String code) {
        return redisCacheKeyRuleClient.isUniqueCode(id, code);
    }

}
