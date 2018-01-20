package com.yihu.ehr.basic.portal.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "portal_feedback", schema = "", catalog = "healtharchive")
public class PortalFeedback {
    private Long id;
    private String clientId;
    private String userId;
    private String content;
    private Date submitDate;
    private Integer flag;
    private String replyContent;
    private String replyUserId;
    private Date replyDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "client_id", nullable = true, insertable = true, updatable = true)
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Basic
    @Column(name = "user_id", nullable = true, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "content", nullable = true, insertable = true, updatable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "submit_date", nullable = true, insertable = true, updatable = true)
    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @Basic
    @Column(name = "flag", nullable = true, insertable = true, updatable = true)
    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Basic
    @Column(name = "reply_content", nullable = true, insertable = true, updatable = true)
    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    @Basic
    @Column(name = "reply_user_id", nullable = true, insertable = true, updatable = true)
    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    @Basic
    @Column(name = "reply_date", nullable = true, insertable = true, updatable = true)
    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalFeedback that = (PortalFeedback) o;

        if (id != that.id) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (submitDate != null ? !submitDate.equals(that.submitDate) : that.submitDate != null) return false;
        if (flag != null ? !flag.equals(that.flag) : that.flag != null) return false;
        if (replyContent != null ? !replyContent.equals(that.replyContent) : that.replyContent != null) return false;
        if (replyUserId != null ? !replyUserId.equals(that.replyUserId) : that.replyUserId != null) return false;
        if (replyDate != null ? !replyDate.equals(that.replyDate) : that.replyDate != null) return false;

        return true;
    }

}
