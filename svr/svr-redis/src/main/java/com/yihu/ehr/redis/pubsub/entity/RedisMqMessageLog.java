package com.yihu.ehr.redis.pubsub.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

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
    public String publisherAppId; // 发布者应用ID
    public String status; // 消息状态，0：未消费，1：已消费
    public String isRealConsumed; // 是否真实被订阅消费，0：否，1：是
    public Integer consumedNum; // 真实消费的次数
    public Date createTime; // 创建时间
    public Date updateTime; // 更新时间

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

    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
