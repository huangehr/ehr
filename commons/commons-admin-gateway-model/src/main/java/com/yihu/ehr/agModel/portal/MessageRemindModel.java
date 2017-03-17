package com.yihu.ehr.agModel.portal;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public class MessageRemindModel {

    private Long id;
    private Integer appId;
    private String appName;
    private String fromUserId;
    private String toUserId;
    private String toUserName;
    private String typeId;
    private String typeName;
    private String content;
    private String workUri;
    private Integer  readed;
    private String createDate;

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {return toUserName;}

    public void setToUserName(String toUserName) {this.toUserName = toUserName;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
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

    public String getTypeName() {return typeName;}

    public void setTypeName(String typeName) {this.typeName = typeName;}

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
