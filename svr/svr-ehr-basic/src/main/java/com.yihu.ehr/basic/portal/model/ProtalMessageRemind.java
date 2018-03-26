package com.yihu.ehr.basic.portal.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 消息提醒
 *
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/20.
 */
@Entity
@Table(name = "portal_message_remind", schema = "", catalog = "healtharchive")
public class ProtalMessageRemind {

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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @Column(name = "to_user_id", nullable = true)
    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "app_id", nullable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "app_name", nullable = true)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Column(name = "from_user_id", nullable = true)
    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    @Column(name = "type_id", nullable = true)
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Column(name = "content", nullable = true)
    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "work_uri", nullable = true)
    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }

    @Column(name = "readed", nullable = true)
    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }

    @Column(name = "create_date", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
}
