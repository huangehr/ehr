package com.yihu.ehr.model.archivesecurity;

import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public class MSmsMessageSend {

    long id;
    String messageTempCode;
    String domain;
    String identityId;
    String phoneNumber;
    Date sendTime;
    Date validTime;
    int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageTempCode() {
        return messageTempCode;
    }

    public void setMessageTempCode(String messageTempCode) {
        this.messageTempCode = messageTempCode;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}