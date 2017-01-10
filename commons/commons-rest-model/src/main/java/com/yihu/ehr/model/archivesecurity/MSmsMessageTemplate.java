package com.yihu.ehr.model.archivesecurity;

import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
public class MSmsMessageTemplate {

    private String id;
    private String messageTempCode;
    private String messageTempName;
    private String messageTemplate;
    private int messageType;
    private Date modifyTime;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getMessageTempCode() {
        return messageTempCode;
    }
    public void setMessageTempCode(String messageTempCode) {
        this.messageTempCode = messageTempCode;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }
    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessageTempName() {
        return messageTempName;
    }
    public void setMessageTempName(String messageTempName) {
        this.messageTempName = messageTempName;
    }

    public int getMessageType() {
        return messageType;
    }
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
