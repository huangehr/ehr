package com.yihu.ehr.redis.pubsub.entity;


import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;

/**
 * redis消息发布者 Entity
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
@Entity
@Table(name = "redis_mq_publisher")
public class RedisMqPublisher extends BaseIdentityEntity {

    public String appId; // 应用ID
    public String authorizedCode; // 授权码
    public String channel; // 消息队列编码
    public String remark; // 备注

    @Column(name = "app_id")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "authorized_code")
    public String getAuthorizedCode() {
        return authorizedCode;
    }

    public void setAuthorizedCode(String authorizedCode) {
        this.authorizedCode = authorizedCode;
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
