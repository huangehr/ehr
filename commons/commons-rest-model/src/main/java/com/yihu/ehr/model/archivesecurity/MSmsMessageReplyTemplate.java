package com.yihu.ehr.model.archivesecurity;

import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
public class MSmsMessageReplyTemplate {

    private String id;
    private String messageReplyCode;
    private String messageReplyName;
    private String messageReplyTemplate;
    private String messageTempCode;
    private Date modifyTime;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getMessageReplyCode() {
        return messageReplyCode;
    }
    public void setMessageReplyCode(String messageReplyCode) {
        this.messageReplyCode = messageReplyCode;
    }

    public String getMessageReplyName() {
        return messageReplyName;
    }
    public void setMessageReplyName(String messageReplyName) {
        this.messageReplyName = messageReplyName;
    }

    public String getMessageReplyTemplate() {
        return messageReplyTemplate;
    }
    public void setMessageReplyTemplate(String messageReplyTemplate) {
        this.messageReplyTemplate = messageReplyTemplate;
    }

    public String getMessageTempCode() {
        return messageTempCode;
    }
    public void setMessageTempCode(String messageTempCode) {
        this.messageTempCode = messageTempCode;
    }

    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
