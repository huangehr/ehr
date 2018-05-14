package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisMqMessageLogService redisMqMessageLogService;
    @Autowired
    private RedisMqSubscriberService redisMqSubscriberService;
    @Autowired
    private RedisMqChannelService redisMqChannelService;

    @Override
    public void handleMessage(String message, String channel) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String messageId = messageMap.get("messageId").toString();
            String publisherAppId = messageMap.get("publisherAppId").toString();
            String messageContent = messageMap.get("messageContent").toString();

            List<RedisMqSubscriber> subscriberList = redisMqSubscriberService.findByChannel(channel);
            if (subscriberList.size() == 0) {
                logger.info("消息订阅（没有订阅者场合），channel: " + channel
                        + ", message: " + messageContent);

                // 累计出列数
                RedisMqChannel mqChannel = redisMqChannelService.findByChannel(channel);
                mqChannel.setDequeuedNum(mqChannel.getDequeuedNum() + 1);
                redisMqChannelService.save(mqChannel);
            } else {
                // 遍历消息队列的订阅者，并推送消息
                for (RedisMqSubscriber subscriber : subscriberList) {
                    String subscribedUrl = subscriber.getSubscribedUrl();

                    logger.info("消息订阅，channel: " + channel + ", subscribedUrl: " + subscribedUrl
                            + ", message: " + messageContent);

                    boolean subFlag = false;
                    try {
                        // 推送消息到指定服务地址
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                        HttpEntity<String> entity = new HttpEntity<>(messageContent, headers);
                        restTemplate.postForObject(subscribedUrl, entity, String.class);
                        subFlag = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (StringUtils.isEmpty(messageId)) {
                            // 头次订阅失败，则记录消息，以便于重试。
                            RedisMqMessageLog messageLog = MessageCommonBiz.newMessageLog(channel, publisherAppId, messageContent);
                            messageLog.setFailedNum(1);
                            redisMqMessageLogService.save(messageLog);
                        } else {
                            // 更新订阅失败次数
                            RedisMqMessageLog messageLog = redisMqMessageLogService.getById(messageId);
                            messageLog.setFailedNum(messageLog.getFailedNum() + 1);
                            redisMqMessageLogService.save(messageLog);
                        }
                    }

                    if (subFlag) {
                        // 累计出列数
                        RedisMqChannel mqChannel = redisMqChannelService.findByChannel(channel);
                        mqChannel.setDequeuedNum(mqChannel.getDequeuedNum() + 1);
                        redisMqChannelService.save(mqChannel);

                        // 发生过订阅失败的场合，订阅成功之后，更新消息状态。
                        if (!StringUtils.isEmpty(messageId)) {
                            RedisMqMessageLog messageLog = redisMqMessageLogService.getById(messageId);
                            messageLog.setStatus(1);
                            redisMqMessageLogService.save(messageLog);
                        }
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
