package com.yihu.ehr.model.redis;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * redis消息订阅者
 *
 * @author 张进军
 * @date 2017/11/13 15:14
 */
public class MRedisMqSubscriber implements Serializable {

    public Integer id; // 主键
    public String appId; // 应用ID
    public String subscribedUrl; // 订阅者服务地址
    public String channel; // 消息队列编码
    public String remark; // 备注
    public Date createDate; // 创建时间
    public String creator; // 创建者
    public Date modifyDate; // 修改时间
    public String modifier; // 修改者

    public MRedisMqSubscriber() {
    }

    public MRedisMqSubscriber(String channel) {
        this.channel = channel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSubscribedUrl() {
        return subscribedUrl;
    }

    public void setSubscribedUrl(String subscribedUrl) {
        this.subscribedUrl = subscribedUrl;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
}
