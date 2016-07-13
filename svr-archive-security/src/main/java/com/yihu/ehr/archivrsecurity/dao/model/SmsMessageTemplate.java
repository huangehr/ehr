package com.yihu.ehr.archivrsecurity.dao.model;

import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
@Entity
@Table(name = "sms_message_template")
public class SmsMessageTemplate {

    private String id;
    private String messageTempCode;
    private String messageTempName;
    private String messageTemplate;
    private int messageType;
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

    @Column(name = "message_temp_code",nullable = false)
    public String getMessageTempCode() {
        return messageTempCode;
    }
    public void setMessageTempCode(String messageTempCode) {
        this.messageTempCode = messageTempCode;
    }

    @Column(name = "message_template",nullable = false)
    public String getMessageTemplate() {
        return messageTemplate;
    }
    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Column(name = "message_temp_name")
    public String getMessageTempName() {
        return messageTempName;
    }
    public void setMessageTempName(String messageTempName) {
        this.messageTempName = messageTempName;
    }

    @Column(name = "message_type")
    public int getMessageType() {
        return messageType;
    }
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    @Column(name = "modify_time")
    @DateTimeFormat(pattern = DateTimeUtil.simpleDateTimePattern)
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
