package com.yihu.ehr.mq;

/**
 * 消息接收器接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 12:41
 */
public interface XReceiver {
    void receive(Object message);
}
