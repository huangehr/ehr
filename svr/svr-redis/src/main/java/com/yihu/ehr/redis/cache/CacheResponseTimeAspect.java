package com.yihu.ehr.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录缓存获取的响应时间
 *
 * @author 张进军
 * @date 2017/12/1 14:23
 */
@Aspect
@Component
public class CacheResponseTimeAspect {

    @Value("${ehr-redis.mq.pubsub.publisherAppId}")
    private String publisherAppId;
    @Value("${ehr-redis.mq.pubsub.channel}")
    private String channel;
    @Autowired
    private RedisMqChannelService redisMqChannelService;
    @Autowired
    private ObjectMapper objectMapper;

    private long startTime;
    private long endTime;

    @Before("execution(* com.yihu.ehr.redis.cache.controller.RedisCacheOperationEndPoint.get(..))")
    public void doBefore(JoinPoint point) {
        startTime = System.currentTimeMillis();
    }

    @After("execution(* com.yihu.ehr.redis.cache.controller.RedisCacheOperationEndPoint.get(..))")
    public void doAfter(JoinPoint point) {
        try {
            endTime = System.currentTimeMillis();

            String keyRuleCode = point.getArgs()[0].toString();
            String ruleParams = point.getArgs()[1] == null ? "" : point.getArgs()[1].toString();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("keyRuleCode", keyRuleCode);
            messageMap.put("ruleParams", ruleParams);
            messageMap.put("responseTime", endTime - startTime);
            String message = objectMapper.writeValueAsString(messageMap);

            redisMqChannelService.sendMessage(publisherAppId, channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
