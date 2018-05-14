package com.yihu.ehr.redis.listener;

import com.yihu.ehr.redis.pubsub.CustomMessageListenerAdapter;
import com.yihu.ehr.redis.pubsub.MessageCommonBiz;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

/**
 * SpringBoot 启动完成的监听器
 *
 * @author 张进军
 * @date 2017/11/14 17:48
 */
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    RedisMessageListenerContainer redisMessageListenerContainer;
    @Autowired
    RedisMqChannelService redisMqChannelService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            // 添加消息队列的监听器
            List<RedisMqChannel> channelList = redisMqChannelService.search("");
            for (RedisMqChannel channel : channelList) {
                ChannelTopic topic = new ChannelTopic(channel.getChannel());
                CustomMessageListenerAdapter messageListener = MessageCommonBiz.newCustomMessageListenerAdapter(channel.getChannel());
                redisMessageListenerContainer.addMessageListener(messageListener, topic);
                try {
                    // 每次添加channel监听器都会flush，这需要点时间。
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
