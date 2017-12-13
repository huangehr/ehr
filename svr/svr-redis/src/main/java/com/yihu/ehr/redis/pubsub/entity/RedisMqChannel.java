package com.yihu.ehr.redis.pubsub.entity;


import com.yihu.ehr.entity.BaseIdentityEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

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

    // 临时属性
    public String enqueuedNum; // 入列数
    public String dequeuedNum; // 出列数
    public String subscriberNum; // 订阅者数
    public String publisherNum; // 发布者数

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

    @Formula("( SELECT COUNT(1) FROM redis_mq_message_log ml WHERE ml.status = 0 AND ml.channel = channel )")
    public String getEnqueuedNum() {
        return enqueuedNum;
    }

    public void setEnqueuedNum(String enqueuedNum) {
        this.enqueuedNum = enqueuedNum;
    }

    @Formula("( SELECT COUNT(1) FROM redis_mq_message_log ml WHERE ml.status = 1 AND ml.channel = channel )")
    public String getDequeuedNum() {
        return dequeuedNum;
    }

    public void setDequeuedNum(String dequeuedNum) {
        this.dequeuedNum = dequeuedNum;
    }

    @Formula("( SELECT COUNT(1) FROM redis_mq_subscriber s WHERE s.channel = channel )")
    public String getSubscriberNum() {
        return subscriberNum;
    }

    public void setSubscriberNum(String subscriberNum) {
        this.subscriberNum = subscriberNum;
    }

    @Formula("( SELECT COUNT(1) FROM redis_mq_publisher mp WHERE mp.channel = channel )")
    public String getPublisherNum() {
        return publisherNum;
    }

    public void setPublisherNum(String publisherNum) {
        this.publisherNum = publisherNum;
    }
}
