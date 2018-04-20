package com.yihu.ehr.model.portal;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private String toUserId;
    private String typeId;
    private String content;
    private String workUri;
    private Integer readed;
    private Date createDate;
    private Long messageTemplateId;
    /**
     * 推送过来的消息
     */
    private String receivedMessages;

    /**
     * 就诊时间
     */
    private String visitTime;
    /**
     * 就诊部门地址
     */
    private String deptAdress;
    /**
     * 温馨提示
     */
    private String notice;
    /**
     * 预约挂号订单id
     */
    private String orderId;
    private String fromUserName;
    private String toUserName;
    private MRegistration mRegistration;
    private String portalMessagerTemplateType;
    /**
     * 我的就诊-是否通知：0为通知，1为不通知。我的档案：0未评价、1为已评价
     */
    private String notifieFlag;
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

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getMessageTemplateId() {
        return messageTemplateId;
    }

    public void setMessageTemplateId(Long messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public String getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(String receivedMessages) {
        this.receivedMessages = receivedMessages;
    }
    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getDeptAdress() {
        return deptAdress;
    }

    public void setDeptAdress(String deptAdress) {
        this.deptAdress = deptAdress;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public MRegistration getmRegistration() {
        return mRegistration;
    }

    public void setmRegistration(MRegistration mRegistration) {
        this.mRegistration = mRegistration;
    }

    public String getPortalMessagerTemplateType() {
        return portalMessagerTemplateType;
    }

    public void setPortalMessagerTemplateType(String portalMessagerTemplateType) {
        this.portalMessagerTemplateType = portalMessagerTemplateType;
    }

    public String getNotifieFlag() {
        return notifieFlag;
    }

    public void setNotifieFlag(String notifieFlag) {
        this.notifieFlag = notifieFlag;
    }
}
