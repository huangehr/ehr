package com.yihu.ehr.redis.pubsub.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * redis消息记录 Entity
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
@Entity
@Table(name = "redis_mq_message_log")
public class RedisMqMessageLog {

    public String id; // 主键
    public String message; // 消息
    public String channel; // 消息队列编码
    public String publisher; // 发布者
    public String status; // 消息状态，0：未消费，1：已消费
    public String createTime; // 创建时间
    public String updateTime; // 更新时间

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Column(name = "publisher")
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
