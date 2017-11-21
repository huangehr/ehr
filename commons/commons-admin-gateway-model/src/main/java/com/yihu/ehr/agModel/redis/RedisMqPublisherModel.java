package com.yihu.ehr.agModel.redis;


import java.io.Serializable;

/**
 * redis消息发布者 model
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
public class RedisMqPublisherModel implements Serializable {

    public Integer id; // 主键
    public String appId; // 应用ID
    public String authorizedCode; // 授权码
    public String channel; // 消息队列编码
    public String createTime; // 创建时间
    public String remark; // 备注

    public RedisMqPublisherModel() {
    }

    public RedisMqPublisherModel(String channel) {
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

    public String getAuthorizedCode() {
        return authorizedCode;
    }

    public void setAuthorizedCode(String authorizedCode) {
        this.authorizedCode = authorizedCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
