package com.yihu.ehr.redis.pubsub.entity;


import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;

/**
 * redis消息订阅者 Entity
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
@Entity
@Table(name = "redis_mq_subscriber")
public class RedisMqSubscriber extends BaseIdentityEntity {

    public String appId; // 应用ID
    public String subscribedUrl; // 订阅者服务地址
    public String channel; // 消息队列编码
    public String remark; // 备注

    @Column(name = "app_id")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "subscribed_url")
    public String getSubscribedUrl() {
        return subscribedUrl;
    }

    public void setSubscribedUrl(String subscribedUrl) {
        this.subscribedUrl = subscribedUrl;
    }

    @Column(name = "channel")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
