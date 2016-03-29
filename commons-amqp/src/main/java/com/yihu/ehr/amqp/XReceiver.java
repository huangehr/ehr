package com.yihu.ehr.amqp;

/**
 * 消息接收器。
 */
public interface XReceiver {
    void receiveMessage(String message);
}
