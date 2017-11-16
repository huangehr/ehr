package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.RedisServiceApp;
import com.yihu.ehr.lang.SpringContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis 消息发布订阅测试
 *
 * @author 张进军
 * @date 2017/11/2 14:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(RedisServiceApp.class)
@WebAppConfiguration
public class PubSubTest {

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;
    @Autowired
    RedisMessageListenerContainer redisMessageListenerContainer;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void redisPubSubTest() throws InterruptedException, JsonProcessingException {
        String channel01 = "zjj.channel.01";
        ChannelTopic c1 = new ChannelTopic(channel01);

        String subscribedUrl = "http://localhost:10000/api/v1.0/admin/redis/mq/subscriber/receiveMessage";
        DefaultMessageDelegate defaultMessageDelegate = new DefaultMessageDelegate(subscribedUrl);
        SpringContext.autowiredBean(defaultMessageDelegate);
        MessageListenerAdapter messageListener = new MessageListenerAdapter();
        messageListener.setDelegate(defaultMessageDelegate);
        SpringContext.autowiredBean(messageListener);
        redisMessageListenerContainer.addMessageListener(messageListener, c1);

        Map<String, Object> message = new HashMap<>();
        message.put("messageLogId", "111");
        message.put("messageContent", "a test message.");
        redisTemplate.convertAndSend(channel01, objectMapper.writeValueAsString(message));
    }

}
