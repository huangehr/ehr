package com.yihu.ehr.resolve.config;

import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.resolve.queue.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import javax.annotation.PostConstruct;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 17:03
 */
//@Configuration
@Deprecated
public class MessageQueueConfig {

    @Autowired
    private RedisMessageListenerContainer container;
    @Autowired
    private MessageListenerAdapter messageListenerAdapter;

    @Bean
    MessageListenerAdapter messageListenerAdapter(MessageReceiver receiver){
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receive");
        return adapter;
    }

    @PostConstruct
    public void init(){
        container.addMessageListener(messageListenerAdapter, new PatternTopic(Channel.PackageResolve));
    }
}
