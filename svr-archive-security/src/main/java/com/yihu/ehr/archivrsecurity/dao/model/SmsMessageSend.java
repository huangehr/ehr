package com.yihu.ehr.archivrsecurity.dao.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Entity
@Table(name = "sms_message_send")
@Access(value = AccessType.PROPERTY)
public class SmsMessageSend {

    long id;
    String messageTempCode;
    String domain;
    String identityId;
    String phoneNumber;
    Date sendTime;
    Date validTime;
    int status;

    public SmsMessageSend() {
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

    @Column(name = "message_temp_code", nullable = false)
    public String getMessageTempCode() {
        return messageTempCode;
    }
    public void setMessageTempCode(String messageTempCode) {
        this.messageTempCode = messageTempCode;
    }

    @Column(name = "domain", nullable = false)
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Column(name = "identity_id", nullable = false)
    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    @Column(name = "phone_number", nullable = false)
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "sendTime", nullable = false)
    public Date getSendTime() {
        return sendTime;
    }
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Column(name = "validTime", nullable = false)
    public Date getValidTime() {
        return validTime;
    }
    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    @Column(name = "status", nullable = false)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}