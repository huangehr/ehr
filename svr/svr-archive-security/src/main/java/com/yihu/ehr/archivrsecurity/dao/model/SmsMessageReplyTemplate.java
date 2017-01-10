package com.yihu.ehr.archivrsecurity.dao.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
@Entity
@Table(name = "sms_message_reply_template")
public class SmsMessageReplyTemplate {

    private String id;
    private String messageReplyCode;
    private String messageReplyName;
    private String messageReplyTemplate;
    private String messageTempCode;
    private Date modifyTime;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",nullable = false,unique = true)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "message_reply_code",nullable = false)
    public String getMessageReplyCode() {
        return messageReplyCode;
    }
    public void setMessageReplyCode(String messageReplyCode) {
        this.messageReplyCode = messageReplyCode;
    }

    @Column(name = "message_reply_name")
    public String getMessageReplyName() {
        return messageReplyName;
    }
    public void setMessageReplyName(String messageReplyName) {
        this.messageReplyName = messageReplyName;
    }

    @Column(name = "message_reply_template",nullable = false)
    public String getMessageReplyTemplate() {
        return messageReplyTemplate;
    }
    public void setMessageReplyTemplate(String messageReplyTemplate) {
        this.messageReplyTemplate = messageReplyTemplate;
    }

    @Column(name = "message_temp_code",nullable = false)
    public String getMessageTempCode() {
        return messageTempCode;
    }
    public void setMessageTempCode(String messageTempCode) {
        this.messageTempCode = messageTempCode;
    }

    @Column(name = "modify_time")
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
