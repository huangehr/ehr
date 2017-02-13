package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "portal_feedback")
@Access(value = AccessType.FIELD)
public class PortalFeedback {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "app_id", unique = true, nullable = false)
    private String appId;

    @Column(name = "status", unique = true, nullable = false)
    private int status;

    @Column(name = "content", unique = true, nullable = false)
    private String content;

    @Column(name = "submit_date", unique = true, nullable = false)
    private Date submitDate;

    @Column(name = "reply_content", unique = true, nullable = false)
    private String replyContent;

    @Column(name = "reply_user_id", unique = true, nullable = false)
    private String replyUserId;

    @Column(name = "reply_date", unique = true, nullable = false)
    private String replyDate;

    public PortalFeedback() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }
}