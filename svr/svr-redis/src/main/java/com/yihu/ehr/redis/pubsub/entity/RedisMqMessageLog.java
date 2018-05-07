package com.yihu.ehr.redis.pubsub.entity;


import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 消费失败的消息记录 Entity
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
    public Integer status; // 消费状态，1：已消费，未消费
    public Integer failedNum; // 订阅失败次数

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
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    @Column(name = "failed_num")
    public Integer getFailedNum() {
        return failedNum;
    }

    public void setFailedNum(Integer failedNum) {
        this.failedNum = failedNum;
    }
}
