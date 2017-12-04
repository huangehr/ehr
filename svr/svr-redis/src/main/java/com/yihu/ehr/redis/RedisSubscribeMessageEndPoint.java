package com.yihu.ehr.redis;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.CacheBizCommon;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import com.yihu.ehr.redis.cache.entity.RedisCacheResponseTimeLog;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyRuleService;
import com.yihu.ehr.redis.cache.service.RedisCacheResponseTimeLogService;
import com.yihu.ehr.util.id.UuidUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 张进军
 * @date 2017/12/4 15:19
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "接收消息队列的消息订阅接口", tags = {"缓存服务管理--接收消息队列的消息订阅接口"})
public class RedisSubscribeMessageEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheResponseTimeLogService responseTimeLogService;
    @Autowired
    private RedisCacheKeyRuleService keyRuleService;

    @ApiOperation("记录缓存获取接口的响应时间")
    @RequestMapping(value = ServiceApi.Redis.SubscribeMessage.ReceiveResponseTime, method = RequestMethod.POST)
    public void receiveResponseTime(
            @ApiParam(value = "消息", required = true)
            @RequestBody String message) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String keyRuleCode = messageMap.get("keyRuleCode").toString();
            String ruleParams = messageMap.get("ruleParams") == null ? "" : messageMap.get("ruleParams").toString();
            int responseTime = (Integer) messageMap.get("responseTime");

            RedisCacheKeyRule redisCacheKeyRule = keyRuleService.findByCode(keyRuleCode);
            if (redisCacheKeyRule != null) {
                String categoryCode = redisCacheKeyRule.getCategoryCode();
                String keyRuleExpression = redisCacheKeyRule.getExpression();
                String key = CacheBizCommon.generateKey(keyRuleExpression, ruleParams, categoryCode);

                RedisCacheResponseTimeLog responseTimeLog = new RedisCacheResponseTimeLog();
                responseTimeLog.setId(UuidUtil.randomUUID());
                responseTimeLog.setCacheKey(key);
                responseTimeLog.setCategoryCode(categoryCode);
                responseTimeLog.setResponseTime(responseTime);
                responseTimeLogService.save(responseTimeLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
