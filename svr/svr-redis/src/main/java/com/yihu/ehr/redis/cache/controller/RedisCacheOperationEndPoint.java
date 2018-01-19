package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.CacheCommonBiz;
import com.yihu.ehr.redis.cache.entity.RedisCacheAuthorization;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import com.yihu.ehr.redis.cache.service.RedisCacheAuthorizationService;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyRuleService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 缓存操作
 *
 * @author 张进军
 * @date 2017/11/23 15:09
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存操作接口", tags = {"缓存服务管理--缓存操作接口"})
public class RedisCacheOperationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheKeyRuleService redisCacheKeyRuleService;
    @Autowired
    private RedisCacheAuthorizationService redisCacheAuthorizationService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation("根据Key规则，获取指定key的缓存值")
    @RequestMapping(value = ServiceApi.Redis.CacheOperation.Get, method = RequestMethod.GET)
    public Envelop get(
            @ApiParam(name = "keyRuleCode", value = "Key规则编码", required = true)
            @RequestParam("keyRuleCode") String keyRuleCode,
            @ApiParam(name = "ruleParams", value = "规则参数，JSON字符串")
            @RequestParam(value = "ruleParams", required = false) String ruleParams) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheKeyRule redisCacheKeyRule = redisCacheKeyRuleService.findByCode(keyRuleCode);
            if (redisCacheKeyRule == null) {
                envelop.setErrorMsg("Key规则 " + keyRuleCode + " 不存在。");
                return envelop;
            }
            String categoryCode = redisCacheKeyRule.getCategoryCode();
            String keyRuleExpression = redisCacheKeyRule.getExpression();

            // 获取缓存值
            ValueOperations<String, Object> valOps = redisTemplate.opsForValue();
            String key = CacheCommonBiz.generateKey(keyRuleExpression, ruleParams, categoryCode);
            Object value = valOps.get(key);

            envelop.setSuccessFlg(true);
            envelop.setObj(value);
            envelop.setErrorMsg("成功获取缓存。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("根据Key规则，设置指定key的缓存值")
    @RequestMapping(value = ServiceApi.Redis.CacheOperation.Set, method = RequestMethod.POST)
    public Envelop set(
            @ApiParam(name = "appId", value = "应用ID", required = true)
            @RequestParam("appId") String appId,
            @ApiParam(name = "value", value = "值", required = true)
            @RequestParam("value") String value,
            @ApiParam(name = "keyRuleCode", value = "Key规则编码", required = true)
            @RequestParam("keyRuleCode") String keyRuleCode,
            @ApiParam(name = "ruleParams", value = "规则参数，JSON字符串")
            @RequestParam(value = "ruleParams", required = false) String ruleParams) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheKeyRule redisCacheKeyRule = redisCacheKeyRuleService.findByCode(keyRuleCode);
            if (redisCacheKeyRule == null) {
                envelop.setErrorMsg("Key规则 " + keyRuleCode + " 不存在。");
                return envelop;
            }
            String categoryCode = redisCacheKeyRule.getCategoryCode();
            String keyRuleExpression = redisCacheKeyRule.getExpression();
            RedisCacheAuthorization redisCacheAuthorization = redisCacheAuthorizationService.findByCategoryCodeAndAppId(categoryCode, appId);

            // 判断应用ID是否在缓存分类上绑定
            if (redisCacheAuthorization == null) {
                envelop.setErrorMsg("该应用ID还未绑定到缓存分类 " + categoryCode + "上，请先绑定。");
                return envelop;
            }

            // 设置缓存值
            ValueOperations<String, Object> valOps = redisTemplate.opsForValue();
            String key = CacheCommonBiz.generateKey(keyRuleExpression, ruleParams, categoryCode);
            valOps.set(key, value);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功设置缓存。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("设置缓存发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("根据Key规则，移除指定key的缓存值")
    @RequestMapping(value = ServiceApi.Redis.CacheOperation.Remove, method = RequestMethod.POST)
    public Envelop remove(
            @ApiParam(name = "keyRuleCode", value = "Key规则编码", required = true)
            @RequestParam("keyRuleCode") String keyRuleCode,
            @ApiParam(name = "ruleParams", value = "规则参数，JSON字符串")
            @RequestParam(value = "ruleParams", required = false) String ruleParams) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheKeyRule redisCacheKeyRule = redisCacheKeyRuleService.findByCode(keyRuleCode);
            if (redisCacheKeyRule == null) {
                envelop.setErrorMsg("Key规则 " + keyRuleCode + " 不存在。");
                return envelop;
            }
            String categoryCode = redisCacheKeyRule.getCategoryCode();
            String keyRuleExpression = redisCacheKeyRule.getExpression();

            // 移除缓存值
            String key = CacheCommonBiz.generateKey(keyRuleExpression, ruleParams, categoryCode);
            redisTemplate.delete(key);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功移除缓存。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
