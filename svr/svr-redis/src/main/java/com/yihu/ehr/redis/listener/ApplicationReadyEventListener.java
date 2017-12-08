package com.yihu.ehr.redis.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.redis.pubsub.CustomMessageListenerAdapter;
import com.yihu.ehr.redis.pubsub.MessageCommonBiz;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    RedisMqSubscriberService redisMqSubscriberService;
    @Autowired
    RedisMqMessageLogService redisMqMessageLogService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            // 开启消息队列，并添加队列的消息监听器
            List<RedisMqChannel> channelList = redisMqChannelService.search("");
            for (RedisMqChannel channel : channelList) {
                ChannelTopic topic = new ChannelTopic(channel.getChannel());
                CustomMessageListenerAdapter messageListener = MessageCommonBiz.newCustomMessageListenerAdapter(channel.getChannel());
                redisMessageListenerContainer.addMessageListener(messageListener, topic);

                // 将未消费的消息重新加入到队列进行发布
                String msgFilter = "channel=" + channel.getChannel() + ";status=0;";
                List<RedisMqMessageLog> messageLogList = redisMqMessageLogService.search(msgFilter);
                for (RedisMqMessageLog messageLog : messageLogList) {
                    // 重新记录消息，并删除该消息的旧记录
                    RedisMqMessageLog redisMqMessageLog = MessageCommonBiz.newMessageLog(channel.getChannel(),
                            messageLog.getPublisherAppId(), messageLog.getMessage());
                    redisMqMessageLogService.saveAndDeleteOld(redisMqMessageLog, messageLog.getId());

                    // 发布消息
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("messageLogId", redisMqMessageLog.getId());
                    messageMap.put("messageContent", messageLog.getMessage());
                    redisTemplate.convertAndSend(channel.getChannel(), objectMapper.writeValueAsString(messageMap));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
