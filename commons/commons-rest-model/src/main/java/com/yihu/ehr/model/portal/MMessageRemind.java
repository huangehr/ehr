package com.yihu.ehr.model.portal;

import java.util.Date;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public class MMessageRemind {

    private Long id;
    private String appId;
    private String appName;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toUserName;
    private String typeId;
    private String content;
    private String workUri;
    private Integer  readed;
    private Date createDate;

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUserName() {return fromUserName;}

    public void setFromUserName(String fromUserName) {this.fromUserName = fromUserName;}

    public String getToUserName() {return toUserName;}

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }

    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
