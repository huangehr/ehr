package com.yihu.ehr.redis.pubsub.entity;


import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * redis消息队列 Entity
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
@Entity
@Table(name = "redis_mq_channel")
public class RedisMqChannel extends BaseIdentityEntity {

    public String channel; // 消息队列编码
    public String channelName; // 消息队列名称
    public String messageTemplate; // 消息模版
    public String remark; // 备注
    public Integer enqueuedNum; // 入列数
    public Integer dequeuedNum; // 出列数
    public Integer subscriberNum; // 订阅者数
    public Integer publisherNum; // 发布者数

    @Column(name = "channel")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Column(name = "channel_name")
    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Column(name = "message_template")
    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "enqueued_num")
    public Integer getEnqueuedNum() {
        return enqueuedNum;
    }

    public void setEnqueuedNum(Integer enqueuedNum) {
        this.enqueuedNum = enqueuedNum;
    }

    @Column(name = "dequeued_num")
    public Integer getDequeuedNum() {
        return dequeuedNum;
    }

    public void setDequeuedNum(Integer dequeuedNum) {
        this.dequeuedNum = dequeuedNum;
    }

    @Column(name = "subscriber_num")
    public Integer getSubscriberNum() {
        return subscriberNum;
    }

    public void setSubscriberNum(Integer subscriberNum) {
        this.subscriberNum = subscriberNum;
    }

    @Column(name = "publisher_num")
    public Integer getPublisherNum() {
        return publisherNum;
    }

    public void setPublisherNum(Integer publisherNum) {
        this.publisherNum = publisherNum;
    }
}
