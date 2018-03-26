package com.yihu.ehr.basic.portal.model;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    private String feedBackType;//消息反馈类型(功能异常、功能优化、新功能建议、其他)
    private String tel;// 联系方式
    private String pigPath;//上传图片(多张图片地址，用逗号隔开)

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    @Basic
    @Column(name = "feedbacktype", nullable = true, insertable = true, updatable = true)
    public String getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(String feedBackType) {
        this.feedBackType = feedBackType;
    }

    @Basic
    @Column(name = "tel", nullable = true, insertable = true, updatable = true)
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "pigpath", nullable = true, insertable = true, updatable = true)
    public String getPigPath() {
        return pigPath;
    }

    public void setPigPath(String pigPath) {
        this.pigPath = pigPath;
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

        if (feedBackType != null ? !feedBackType.equals(that.feedBackType) : that.feedBackType != null) return false;
        if (tel != null ? !tel.equals(that.tel) : that.tel != null) return false;
        if (pigPath != null ? !pigPath.equals(that.pigPath) : that.pigPath != null) return false;

        return true;
    }

}
