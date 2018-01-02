package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheKeyRule;
import com.yihu.ehr.redis.cache.CacheCommonBiz;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyRuleService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MRedisCacheKeyRule mRedisCacheKeyRule = convertToModel(redisCacheKeyRuleService.getById(id), MRedisCacheKeyRule.class);
            envelop.setObj(mRedisCacheKeyRule);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取缓存Key规则。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存Key规则发生异常：" + e.getMessage());
        }
        return envelop;
    }

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
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<RedisCacheKeyRule> redisCacheKeyRuleList = redisCacheKeyRuleService.search(fields, filters, sorts, page, size);
            int count = (int) redisCacheKeyRuleService.getCount(filters);
            List<MRedisCacheKeyRule> mRedisCacheKeyRule = (List<MRedisCacheKeyRule>) convertToModels(redisCacheKeyRuleList, new ArrayList<MRedisCacheKeyRule>(), MRedisCacheKeyRule.class, fields);
            envelop = getPageResult(mRedisCacheKeyRule, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取缓存Key规则列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存Key规则发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "缓存Key规则JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheKeyRule newEntity = objectMapper.readValue(entityJson, RedisCacheKeyRule.class);
            CacheCommonBiz.validateKeyRule(newEntity.getExpression());
            newEntity = redisCacheKeyRuleService.save(newEntity);

            MRedisCacheKeyRule mRedisCacheKeyRule = convertToModel(newEntity, MRedisCacheKeyRule.class);
            envelop.setObj(mRedisCacheKeyRule);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增缓存Key规则。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增缓存Key规则发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "缓存Key规则JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheKeyRule updateEntity = objectMapper.readValue(entityJson, RedisCacheKeyRule.class);
            CacheCommonBiz.validateKeyRule(updateEntity.getExpression());
            updateEntity = redisCacheKeyRuleService.save(updateEntity);

            MRedisCacheKeyRule mRedisCacheKeyRule = convertToModel(updateEntity, MRedisCacheKeyRule.class);
            envelop.setObj(mRedisCacheKeyRule);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新缓存Key规则。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新缓存Key规则发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除缓存Key规则")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            redisCacheKeyRuleService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除缓存Key规则。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除缓存Key规则发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证缓存Key规则名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存Key规则名称", required = true)
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisCacheKeyRuleService.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该缓存Key规则名称已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证缓存Key规则编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存Key规则编码", required = true)
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisCacheKeyRuleService.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该缓存Key规则编码已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证类似的缓存Key规则表达式是否已经存在")
    @RequestMapping(value = ServiceApi.Redis.CacheKeyRule.IsUniqueExpression, method = RequestMethod.GET)
    public Envelop isUniqueExpression(
            @ApiParam(name = "id", value = "缓存Key规则ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "categoryCode", value = "缓存分类编码", required = true)
            @RequestParam(value = "categoryCode") String categoryCode,
            @ApiParam(name = "expression", value = "缓存Key规则表达式", required = true)
            @RequestParam(value = "expression") String expression) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisCacheKeyRuleService.isUniqueExpression(id, categoryCode, expression);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("当前分类下类似的缓存Key规则表达式已经定义过，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
