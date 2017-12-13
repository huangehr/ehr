package com.yihu.ehr.redis.pubsub;

import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.util.id.UuidUtil;

/**
 * 消息发布订阅共通业务方法
 *
 * @author 张进军
 * @date 2017/11/20 17:03
 */
public class MessageCommonBiz {

    /**
     *  实例化一个 RedisMqMessageLog
     * @param channel 消息队列编码
     * @param publisherAppId 发布者应用ID
     * @param message 要发布的消息
     * @return RedisMqMessageLog
     */
    public static RedisMqMessageLog newMessageLog(String channel, String publisherAppId, String message) {
        RedisMqMessageLog redisMqMessageLog = new RedisMqMessageLog();
        redisMqMessageLog.setId(UuidUtil.randomUUID());
        redisMqMessageLog.setChannel(channel);
        redisMqMessageLog.setPublisherAppId(publisherAppId);
        redisMqMessageLog.setMessage(message);
        redisMqMessageLog.setStatus("0");
        redisMqMessageLog.setIsRealConsumed("0");
        redisMqMessageLog.setConsumedNum(0);
        return redisMqMessageLog;
    }

    /**
     *  实例化一个 CustomMessageListenerAdapter
     * @param channel 消息队列编码
     * @return CustomMessageListenerAdapter
     */
    public static CustomMessageListenerAdapter newCustomMessageListenerAdapter(String channel) {
        DefaultMessageDelegate defaultMessageDelegate = new DefaultMessageDelegate(channel);
        SpringContext.autowiredBean(defaultMessageDelegate);
        CustomMessageListenerAdapter messageListener = new CustomMessageListenerAdapter(defaultMessageDelegate);
        SpringContext.autowiredBean(messageListener);
        return messageListener;
    }

}
