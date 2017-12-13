package com.yihu.ehr.model.redis;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

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
    public String messageTemplate; // 消息模版
    public String remark; // 备注
    public Date createDate; // 创建时间
    public String creator; // 创建者
    public Date modifyDate; // 修改时间
    public String modifier; // 修改者
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

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
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
