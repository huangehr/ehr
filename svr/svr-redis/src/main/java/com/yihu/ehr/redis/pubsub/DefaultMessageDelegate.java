package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Redis 订阅发布的消息代理
 *
 * @author 张进军
 * @date 2017/11/3 10:51
 */
public class DefaultMessageDelegate implements MessageDelegate {

    Logger logger = Logger.getLogger(DefaultMessageDelegate.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisMqMessageLogService redisMqMessageLogService;
    @Autowired
    private RedisMqSubscriberService redisMqSubscriberService;

    // 消息队列编码
    private String channel;

    public DefaultMessageDelegate(String channel) {
        this.channel = channel;
    }

    @Override
    public void handleMessage(String message, String channel) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String messageLogId = messageMap.get("messageLogId").toString();
            String messageContent = messageMap.get("messageContent").toString();

            List<RedisMqSubscriber> subscriberList = redisMqSubscriberService.findByChannel(channel);
            if (subscriberList.size() == 0) {
                // 消息队列没有订阅者的场合，
                RedisMqMessageLog redisMqMessageLog = redisMqMessageLogService.getById(messageLogId);
                redisMqMessageLog.setStatus("1");
                redisMqMessageLog.setUpdateTime(new Date());
                redisMqMessageLogService.save(redisMqMessageLog);
            } else {
                // 遍历消息队列的订阅者，并推送消息
                for (RedisMqSubscriber subscriber : subscriberList) {
                    String subscribedUrl = subscriber.getSubscribedUrl();

                    // 推送消息到指定服务地址
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                    HttpEntity<String> entity = new HttpEntity<>(messageContent, headers);
                    restTemplate.exchange(subscribedUrl, HttpMethod.POST, entity, String.class);

                    // 更新消息状态为已消费
                    RedisMqMessageLog redisMqMessageLog = redisMqMessageLogService.getById(messageLogId);
                    int oldConsumeNum = redisMqMessageLog.getConsumedNum();
                    redisMqMessageLog.setStatus("1");
                    redisMqMessageLog.setIsRealConsumed("1");
                    redisMqMessageLog.setConsumedNum(oldConsumeNum + 1);
                    redisMqMessageLog.setUpdateTime(new Date());
                    redisMqMessageLogService.save(redisMqMessageLog);

                    logger.info("\n--- Redis发布订阅消费的消息 ---\nchannel: " + channel
                            + ", messageLogId: " + messageLogId
                            + ", subscribedUrl: " + subscribedUrl
                            + ", message: " + messageContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
