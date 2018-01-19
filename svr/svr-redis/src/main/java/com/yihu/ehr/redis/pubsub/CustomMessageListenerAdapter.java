package com.yihu.ehr.redis.pubsub;

import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 重写父类 MessageListenerAdapter 的 equals() 方法
 *
 * @author 张进军
 * @date 2017/11/16 17:33
 */
public class CustomMessageListenerAdapter extends MessageListenerAdapter {

    // 消息队列编码
    private String channel;

    public CustomMessageListenerAdapter(DefaultMessageDelegate delegate) {
        super.setDelegate(delegate);
        this.channel = super.getDelegate().toString();
    }

    public int hashCode() {
        return this.channel.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomMessageListenerAdapter)) {
            return false;
        } else {
            CustomMessageListenerAdapter other = (CustomMessageListenerAdapter) obj;
            if (this.channel == null) {
                if (other.channel != null) {
                    return false;
                }
            } else if (!this.channel.equals(other.channel)) {
                return false;
            }

            return true;
        }
    }

}
