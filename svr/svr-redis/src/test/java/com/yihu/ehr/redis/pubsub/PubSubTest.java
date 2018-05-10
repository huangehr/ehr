package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.RedisServiceApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Redis消息发布订阅测试
 *
 * @author 张进军
 * @date 2017/11/2 14:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisServiceApp.class)
public class PubSubTest {

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;
    @Autowired
    RedisMessageListenerContainer redisMessageListenerContainer;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void redisPubSubTest() throws InterruptedException, JsonProcessingException {
        String channel_01 = "zjj.test.01";
        for (int i = 0; i < 8000; i++) {
            String channel = channel_01 + i;
            ChannelTopic topic = new ChannelTopic(channel);

            CustomMessageListenerAdapter messageListener = MessageCommonBiz.newCustomMessageListenerAdapter(channel);
            redisMessageListenerContainer.addMessageListener(messageListener, topic);
        }

//        Map<String, Object> message = new HashMap<>();
//        message.put("messageLogId", "");
//        message.put("messageContent", "a test message.");
//        redisTemplate.convertAndSend(channel_01, objectMapper.writeValueAsString(message));
    }

}
