package com.yihu.ehr.model.redis;


import java.io.Serializable;

/**
 * redis消息队列
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
public class MRedisMqChannel implements Serializable{

    public Integer id; // 主键
    public String channel; // 消息队列编码
    public String channelName; // 消息队列名称
    public String authorizedCode; // 授权码
    public String remark; // 备注
    public String enqueuedNum; // 入列数
    public String dequeuedNum; // 出列数
    public String subscriberNum; // 订阅者数
    public String publisherNum; // 发布者数

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getAuthorizedCode() {
        return authorizedCode;
    }

    public void setAuthorizedCode(String authorizedCode) {
        this.authorizedCode = authorizedCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEnqueuedNum() {
        return enqueuedNum;
    }

    public void setEnqueuedNum(String enqueuedNum) {
        this.enqueuedNum = enqueuedNum;
    }

    public String getDequeuedNum() {
        return dequeuedNum;
    }

    public void setDequeuedNum(String dequeuedNum) {
        this.dequeuedNum = dequeuedNum;
    }

    public String getSubscriberNum() {
        return subscriberNum;
    }

    public void setSubscriberNum(String subscriberNum) {
        this.subscriberNum = subscriberNum;
    }

    public String getPublisherNum() {
        return publisherNum;
    }

    public void setPublisherNum(String publisherNum) {
        this.publisherNum = publisherNum;
    }
}
