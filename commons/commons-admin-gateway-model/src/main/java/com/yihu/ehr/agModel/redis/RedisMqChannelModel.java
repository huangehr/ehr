package com.yihu.ehr.agModel.redis;


import java.io.Serializable;

/**
 * redis消息队列 model
 *
 * @author 张进军
 * @date 2017/11/10 11:14
 */
public class RedisMqChannelModel implements Serializable {

    public Integer id; // 主键
    public String channel; // 消息队列编码
    public String channelName; // 消息队列名称
    public String authorizedCode; // 授权码
    public String remark; // 备注

    public RedisMqChannelModel() {
    }

    public RedisMqChannelModel(String channel) {
        this.channel = channel;
    }

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
}
