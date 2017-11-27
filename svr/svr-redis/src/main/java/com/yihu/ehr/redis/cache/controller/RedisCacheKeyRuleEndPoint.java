package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheKeyRule;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyRuleService;
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
 * 缓存Key规则 接口
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存Key规则接口", tags = {"缓存服务管理--缓存Key规则接口"})
public class RedisCacheKeyRuleEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheKeyRuleService redisCacheKeyRuleService;

    @ApiOperation("根据ID获取缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.GetById, method = RequestMethod.GET)
    public MRedisCacheKeyRule getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(redisCacheKeyRuleService.getById(id), MRedisCacheKeyRule.class);
    }

    @ApiOperation(value = "根据条件获取缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Search, method = RequestMethod.GET)
    List<MRedisCacheKeyRule> search(
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
        List<RedisCacheKeyRule> redisMqChannels = redisCacheKeyRuleService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, redisCacheKeyRuleService.getCount(filters), page, size);
        return (List<MRedisCacheKeyRule>) convertToModels(redisMqChannels, new ArrayList<MRedisCacheKeyRule>(), MRedisCacheKeyRule.class, fields);
    }

    @ApiOperation("新增缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.POST)
    public MRedisCacheKeyRule add(
            @ApiParam(name = "entityJson", value = "缓存Key规则JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisCacheKeyRule newRedisCacheKeyRule = toEntity(entityJson, RedisCacheKeyRule.class);
        newRedisCacheKeyRule = redisCacheKeyRuleService.save(newRedisCacheKeyRule);
        return convertToModel(newRedisCacheKeyRule, MRedisCacheKeyRule.class);
    }

    @ApiOperation("更新缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.PUT)
    public MRedisCacheKeyRule update(
            @ApiParam(name = "entityJson", value = "缓存Key规则JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisCacheKeyRule updateRedisCacheKeyRule = toEntity(entityJson, RedisCacheKeyRule.class);
        updateRedisCacheKeyRule = redisCacheKeyRuleService.save(updateRedisCacheKeyRule);
        return convertToModel(updateRedisCacheKeyRule, MRedisCacheKeyRule.class);
    }

    @ApiOperation("删除缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        redisCacheKeyRuleService.delete(id);
    }

    @ApiOperation("验证缓存Key规则名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueName, method = RequestMethod.GET)
    public boolean isUniqueName(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存Key规则名称", required = true)
            @RequestParam(value = "name") String name) throws Exception {
        return redisCacheKeyRuleService.isUniqueName(id, name);
    }

    @ApiOperation("验证缓存Key规则编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueCode, method = RequestMethod.GET)
    public boolean isUniqueCode(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存Key规则编码", required = true)
            @RequestParam(value = "code") String code) throws Exception {
        return redisCacheKeyRuleService.isUniqueCode(id, code);
    }

}
