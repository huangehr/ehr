package com.yihu.ehr.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.util.rest.Envelop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
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
    @Value("${ehr-redis.mq.pubsub.responseTimeChannel}")
    private String responseTimeChannel;
    @Autowired
    private RedisMqChannelService redisMqChannelService;
    @Autowired
    private ObjectMapper objectMapper;

    private long startTime;
    private long endTime;
    private boolean resultFlag;

    @Before("execution(* com.yihu.ehr.redis.cache.controller.RedisCacheOperationEndPoint.get(..))")
    public void doBefore(JoinPoint point) {
        startTime = System.currentTimeMillis();
    }

    @Around("execution(* com.yihu.ehr.redis.cache.controller.RedisCacheOperationEndPoint.get(..))")
    public void doAround(ProceedingJoinPoint point) throws Throwable {
        Envelop backEnvelop = (Envelop) point.proceed();
        resultFlag = backEnvelop.isSuccessFlg();
    }

    @After("execution(* com.yihu.ehr.redis.cache.controller.RedisCacheOperationEndPoint.get(..))")
    public void doAfter(JoinPoint point) throws Throwable {
        try {
            if (resultFlag) {
                endTime = System.currentTimeMillis();

                String keyRuleCode = point.getArgs()[0].toString();
                String ruleParams = point.getArgs()[1] == null ? "" : point.getArgs()[1].toString();

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("keyRuleCode", keyRuleCode);
                messageMap.put("ruleParams", ruleParams);
                messageMap.put("responseTime", endTime - startTime);
                String message = objectMapper.writeValueAsString(messageMap);

                redisMqChannelService.sendMessage(publisherAppId, responseTimeChannel, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
