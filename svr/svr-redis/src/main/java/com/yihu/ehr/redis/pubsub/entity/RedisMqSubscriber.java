package com.yihu.ehr.redis.pubsub.entity;


import javax.persistence.*;

/**
 * redis消息订阅者 Entity
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
@Entity
@Table(name = "redis_mq_subscriber")
public class RedisMqSubscriber {

    public Integer id; // 主键
    public String appId; // 应用ID
    public String subscribedUrl; // 订阅者服务地址
    public String channel; // 消息队列编码
    public String createTime; // 创建时间
    public String remark; // 备注

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    @Column(name = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
