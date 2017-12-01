package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.redis.cache.entity.RedisCacheAuthorization;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import com.yihu.ehr.redis.cache.service.RedisCacheAuthorizationService;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyRuleService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private RedisTemplate redisTemplate;

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
            String key = generateKey(keyRuleExpression, ruleParams, categoryCode);
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
            String key = generateKey(keyRuleExpression, ruleParams, categoryCode);
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
            String key = generateKey(keyRuleExpression, ruleParams, categoryCode);
            redisTemplate.delete(key);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功移除缓存。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存发生异常：" + e.getMessage());
        }
        return envelop;
    }

    /**
     * 生成缓存的 key
     * 最终生成的key为：[ + categoryCode + ] + 填充值的keyRuleExpression
     *
     * @param keyRuleExpression Key规则表达式
     * @param ruleParams        Key规则参数
     * @param categoryCode      缓存分类编码
     * @return 缓存的 key
     * @throws IOException
     */
    private String generateKey(String keyRuleExpression, String ruleParams, String categoryCode) throws Exception {
        List<String> paramNames = this.parseParamNames(keyRuleExpression);
        String key = keyRuleExpression;
        if (paramNames.size() != 0) {
            if(StringUtils.isEmpty(ruleParams)) {
                String errorMsg = "Key规则表达式中有占位命名参数，则规则参数不能为空。";
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
            }

            Map<String, Object> paramValues = objectMapper.readValue(ruleParams, Map.class);
            for (int i = 0, length = paramNames.size(); i < length; i++) {
                String paramName = paramNames.get(i);
                Object paramValue = paramValues.get(paramName);
                if (paramValue == null) {
                    String errorMsg = "参数名 " + paramName + " 缺少值，或参数名错误，或Key规则不符合规范。";
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
                } else {
                    key = key.replace("{" + paramName + "}", paramValue.toString());
                }
            }
        }
        key = "[" + categoryCode + "]" + key;
        return key;
    }

    /**
     * 解析Key规则中的占位参数名。
     *
     * 例：规则 xxx{a}xx{b}{c},包含三个占位命名参数 a、b、c，
     * 命名参数用“{}”裹着。
     *
     * @param keyRuleExpression Key规则表达式
     * @return 占位参数名集合
     */
    private List<String> parseParamNames(String keyRuleExpression) {
        List<String> paramNames = new ArrayList<>();

        if (!keyRuleExpression.contains("{")) {
            return paramNames;
        }

        int preIndex = 0;
        while (preIndex != -1) {
            int start = keyRuleExpression.indexOf("{", preIndex);
            int end = keyRuleExpression.indexOf("}", preIndex);
            String paramName = keyRuleExpression.substring(start + 1, end);
            paramNames.add(paramName);
            preIndex = keyRuleExpression.indexOf("{", end);
        }

        return paramNames;
    }

}
