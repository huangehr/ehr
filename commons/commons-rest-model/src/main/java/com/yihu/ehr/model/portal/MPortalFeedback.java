package com.yihu.ehr.model.portal;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yeshijie on 2017/2/21.
 */
public class MPortalFeedback implements Serializable {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public String getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(String feedBackType) {
        this.feedBackType = feedBackType;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPigPath() {
        return pigPath;
    }

    public void setPigPath(String pigPath) {
        this.pigPath = pigPath;
    }
}
