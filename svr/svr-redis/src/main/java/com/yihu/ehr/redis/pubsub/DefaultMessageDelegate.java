package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Redis 订阅发布的消息代理
 *
 * @author 张进军
 * @date 2017/11/3 10:51
 */
public class DefaultMessageDelegate implements MessageDelegate {

    private static final Logger logger = Logger.getLogger(DefaultMessageDelegate.class);

    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisMqSubscriberService redisMqSubscriberService;

    @Override
    public void handleMessage(String message, String channel) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String messageId = messageMap.get("messageId").toString();
            String publisherAppId = messageMap.get("publisherAppId").toString();
            String messageContent = messageMap.get("messageContent").toString();

            List<RedisMqSubscriber> subscriberList = redisMqSubscriberService.findByChannel(channel);
            if (subscriberList.size() == 0) {
                // 收集订阅成功的消息，定时任务里累计出列数
                RedisMqMessageLog messageLog = MessageCommonBiz.newMessageLog(channel, publisherAppId, messageContent);
                redisTemplate.opsForList().leftPush(RedisCollection.SUB_SUCCESSFUL_MESSAGES, objectMapper.writeValueAsString(messageLog));
            } else {
                // 遍历消息队列的订阅者，并推送消息
                for (RedisMqSubscriber subscriber : subscriberList) {
                    String subscribedUrl = subscriber.getSubscribedUrl();

                    try {
                        // 推送消息到指定服务地址
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                        HttpEntity<String> entity = new HttpEntity<>(messageContent, headers);
                        restTemplate.postForObject(subscribedUrl, entity, String.class);

                        // 收集订阅成功的消息，定时任务里累计出列数
                        RedisMqMessageLog messageLog = MessageCommonBiz.newMessageLog(channel, publisherAppId, messageContent);
                        if (!StringUtils.isEmpty(messageId)) {
                            // 首次订阅失败，但重试订阅成功场合
                            messageLog.setId(messageId);
                        }
                        redisTemplate.opsForList().leftPush(RedisCollection.SUB_SUCCESSFUL_MESSAGES, objectMapper.writeValueAsString(messageLog));
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 收集订阅失败的消息
                        RedisMqMessageLog messageLog = MessageCommonBiz.newMessageLog(channel, publisherAppId, messageContent);
                        messageLog.setErrorMsg(e.toString());
                        if (!StringUtils.isEmpty(messageId)) {
                            // 非头次订阅失败
                            messageLog.setId(messageId);
                            // 通过 -1 标记为非头次订阅失败，定时任务里累计更新失败次数。
                            messageLog.setFailedNum(-1);
                        }
                        redisTemplate.opsForList().leftPush(RedisCollection.SUB_FAILED_MESSAGES, objectMapper.writeValueAsString(messageLog));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 消息队列编码
    private String channel;

    public DefaultMessageDelegate(String channel) {
        this.channel = channel;
    }

    public int hashCode() {
        return this.channel.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof DefaultMessageDelegate)) {
            return false;
        } else {
            DefaultMessageDelegate other = (DefaultMessageDelegate) obj;
            if (this.channel == null) {
                if (other.channel != null) {
                    return false;
                }
            } else if (!this.channel.equals(other.channel)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return this.channel;
    }

}
