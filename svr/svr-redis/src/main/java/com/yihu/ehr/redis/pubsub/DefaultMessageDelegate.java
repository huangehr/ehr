package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
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

    // 订阅者回调服务地址
    private String subscribedUrl;

    public DefaultMessageDelegate(String subscribedUrl) {
        this.subscribedUrl = subscribedUrl;
    }

    @Override
    public void handleMessage(String message, String channel) {
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String messageLogId = messageMap.get("messageLogId").toString();
            String messageContent = messageMap.get("messageContent").toString();

            // 推送消息到指定服务地址
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<>(messageContent, headers);
            restTemplate.exchange(subscribedUrl, HttpMethod.POST, entity, String.class);

            // 更新消息状态为已消费
            RedisMqMessageLog redisMqMessageLog = redisMqMessageLogService.getById(messageLogId);
            redisMqMessageLog.setStatus("1");
            redisMqMessageLog.setUpdateTime(DateTimeUtil.iso8601DateTimeFormat(new Date()));
            redisMqMessageLogService.save(redisMqMessageLog);

            logger.info("\n--- Redis发布订阅消费的消息 ---\nchannel: " + channel +
                    ", messageLogId: " + messageLogId + ",subscribedUrl: " + subscribedUrl + ",  message: " + messageContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int hashCode() {
        return this.subscribedUrl.hashCode();
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
            if (this.subscribedUrl == null) {
                if (other.subscribedUrl != null) {
                    return false;
                }
            } else if (!this.subscribedUrl.equals(other.subscribedUrl)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return this.subscribedUrl;
    }

}
