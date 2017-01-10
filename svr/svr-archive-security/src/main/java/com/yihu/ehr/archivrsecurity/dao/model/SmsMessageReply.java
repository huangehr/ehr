package com.yihu.ehr.archivrsecurity.dao.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Entity
@Table(name = "sms_message_reply")
@Access(value = AccessType.PROPERTY)
public class SmsMessageReply {

    long id;
    String messageSendId;
    String phoneNumber;
    String messageReply;
    Date replyTime;
    int status;

    public SmsMessageReply() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "message_send_id", nullable = false)
    public String getMessageSendId() {
        return messageSendId;
    }
    public void setMessageSendId(String messageSendId) {
        this.messageSendId = messageSendId;
    }

    @Column(name = "phone_number", nullable = false)
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "message_reply", nullable = false)
    public String getMessageReply() {
        return messageReply;
    }
    public void setMessageReply(String messageReply) {
        this.messageReply = messageReply;
    }

    @Column(name = "reply_time", nullable = false)
    public Date getReplyTime() {
        return replyTime;
    }
    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    @Column(name = "status", nullable = false)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}