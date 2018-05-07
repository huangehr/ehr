package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时定量重试订阅失败的消息
 *
 * @author 张进军
 * @date 2018/5/7 14:01
 */
@Component
public class ResendFailedMessageJob {

    @Autowired
    RedisMqMessageLogService redisMqMessageLogService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    /*
     * 将订阅失败的消息重新加入到队列进行发布
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void resend() {
        try {
            // 每次最多取一千条最早并且少于3次订阅失败的消息，重新加入到队列进行发布。
            List<RedisMqMessageLog> messageLogList = redisMqMessageLogService.search("", "status=0;failedNum<3", "+createDate", 1, 1000);
            for (RedisMqMessageLog messageLog : messageLogList) {
                // 发布消息
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("messageId", messageLog.getId());
                messageMap.put("publisherAppId", messageLog.getPublisherAppId());
                messageMap.put("messageContent", messageLog.getMessage());
                redisTemplate.convertAndSend(messageLog.getChannel(), objectMapper.writeValueAsString(messageMap));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
