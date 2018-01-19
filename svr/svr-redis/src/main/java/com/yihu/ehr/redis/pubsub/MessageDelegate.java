package com.yihu.ehr.redis.pubsub;

/**
 * Redis 订阅发布的消息代理接口
 *
 * @author 张进军
 * @date 2017/11/2 13:43
 */
public interface MessageDelegate {

    void handleMessage(String message, String channel);

}
