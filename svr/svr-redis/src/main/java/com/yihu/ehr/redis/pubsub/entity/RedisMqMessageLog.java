package com.yihu.ehr.redis.pubsub.entity;


import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * redis消息记录 Entity
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
@Entity
@Table(name = "redis_mq_message_log")
public class RedisMqMessageLog extends BaseAssignedEntity {

    public String message; // 消息
    public String channel; // 消息队列编码
    public String publisherAppId; // 发布者应用ID
    public String status; // 消息状态，0：未消费，1：已消费
    public String isRealConsumed; // 是否真实被订阅消费，0：否，1：是
    public Integer consumedNum; // 真实消费的次数

    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "channel")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Column(name = "publisher_app_id")
    public String getPublisherAppId() {
        return publisherAppId;
    }

    public void setPublisherAppId(String publisherAppId) {
        this.publisherAppId = publisherAppId;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "is_real_consumed")
    public String getIsRealConsumed() {
        return isRealConsumed;
    }

    public void setIsRealConsumed(String isRealConsumed) {
        this.isRealConsumed = isRealConsumed;
    }

    @Column(name = "consumed_num")
    public Integer getConsumedNum() {
        return consumedNum;
    }

    public void setConsumedNum(Integer consumedNum) {
        this.consumedNum = consumedNum;
    }
}
