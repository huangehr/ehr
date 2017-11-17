package com.yihu.ehr.redis.pubsub;

import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 重写父类 MessageListenerAdapter 的 equals() 方法
 *
 * @author 张进军
 * @date 2017/11/16 17:33
 */
public class CustomMessageListenerAdapter extends MessageListenerAdapter {

    private String subscribedUrl;

    public CustomMessageListenerAdapter(MessageDelegate delegate) {
        super.setDelegate(delegate);
        this.subscribedUrl = super.getDelegate().toString();
    }

    public int hashCode() {
        return this.subscribedUrl.hashCode();
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
            if (this.subscribedUrl == null) {
                if (other.subscribedUrl != null) {
                    return false;
                }
            } else if (!this.subscribedUrl.equals(other.subscribedUrl)) {
                return false;
            }

            return true;
        }
    }

}
