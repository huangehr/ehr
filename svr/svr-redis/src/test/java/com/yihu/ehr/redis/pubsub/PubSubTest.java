package com.yihu.ehr.redis.pubsub;

import com.yihu.ehr.RedisServiceApp;
import com.yihu.ehr.utils.SpringUtil;
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
    RedisMessageListenerContainer container;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void redisPubSubTest() throws InterruptedException {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(jedisConnectionFactory);
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
//        SpringUtil.autowiredBean(redisTemplate);

        String channel01 = "zjj.channel.01";
        ChannelTopic c1 = new ChannelTopic(channel01);

        MessageListenerAdapter messageListener = new MessageListenerAdapter();
        messageListener.setDelegate(new DefaultMessageDelegate("url-1"));
        SpringUtil.autowiredBean(messageListener);

        MessageListenerAdapter messageListener2 = new MessageListenerAdapter();
        messageListener2.setDelegate(new DefaultMessageDelegate("url-2"));
        SpringUtil.autowiredBean(messageListener2);

        container.addMessageListener(messageListener, c1);
        container.addMessageListener(messageListener2, c1);
        Thread.sleep(50);

        redisTemplate.convertAndSend(channel01, "zjj redis test1.");
    }

    @Test
    public void redisOpsListTest() {
//        ListOperations<String, Object> listOps = redisTemplate.opsForList();
    }

}
