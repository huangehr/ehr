package com.yihu.ehr.resource.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 健康上饶app、公众健康服务平台-满意度调查
 *
 * @author zdm
 * @vsrsion 1.0
 * Created at 2018/4/20.
 */
@Entity
@Table(name = "portal_message_remind", schema = "", catalog = "healtharchive")
public class ProtalMessageRemind {

    private Long id;
    private String appId;
    private String appName;
    private String fromUserId;
    private String typeId;
    private String content;
    private String workUri;
    private Integer readed;
    private Date createDate;
    private String toUserId;
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
     * 健康之路-预约挂号订单号
     */
    private String orderId;
    /**
     * 我的就诊-是否通知：0为通知，1为不通知。我的档案：0未评价、1为已评价
     */
    private String notifieFlag;
    /**
     * 总部推送消息类型 ，101：挂号结果推送，102：退号结果推送，-101：订单操作推送，100：满意度调查
     */
    private String portalMessagerTemplateType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "app_id", nullable = true, insertable = true, updatable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    @Column(name = "app_name", nullable = true, insertable = true, updatable = true)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    @Column(name = "from_user_id", nullable = true, insertable = true, updatable = true)
    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    @Column(name = "type_id", nullable = true, insertable = true, updatable = true)
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    @Column(name = "content", nullable = true, insertable = true, updatable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Column(name = "work_uri", nullable = true, insertable = true, updatable = true)
    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }
    @Column(name = "readed", nullable = true, insertable = true, updatable = true)
    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }
    @Column(name = "create_date", nullable = true, insertable = true, updatable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    @Column(name = "to_user_id", nullable = true, insertable = true, updatable = true)
    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    @Column(name = "message_template_id", nullable = true, insertable = true, updatable = true)
    public Long getMessageTemplateId() {
        return messageTemplateId;
    }

    public void setMessageTemplateId(Long messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }
    @Column(name = "received_messages", nullable = true, insertable = true, updatable = true)
    public String getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(String receivedMessages) {
        this.receivedMessages = receivedMessages;
    }
    @Column(name = "visit_time", nullable = true, insertable = true, updatable = true)
    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }
    @Column(name = "order_id", nullable = true, insertable = true, updatable = true)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    @Column(name = "notifie_flag", nullable = true, insertable = true, updatable = true)
    public String getNotifieFlag() {
        return notifieFlag;
    }

    public void setNotifieFlag(String notifieFlag) {
        this.notifieFlag = notifieFlag;
    }
    @Column(name = "portal_messager_template_type", nullable = true, insertable = true, updatable = true)
    public String getPortalMessagerTemplateType() {
        return portalMessagerTemplateType;
    }

    public void setPortalMessagerTemplateType(String portalMessagerTemplateType) {
        this.portalMessagerTemplateType = portalMessagerTemplateType;
    }

}
