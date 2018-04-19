package com.yihu.ehr.model.portal;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.22
 */
public class MMyMessage {
    /**
     * 消息Id
     */
    private Long id;

    private Date createDate;

    private Integer readed;

    private String typeId;

    private String appId;

    private String appName;

    private String fromUserId;

    private String toUserId;

    private String content;

    private String workUri;

    /**
     * 消息标题
     */
    private String title;
    /**
     * 主体之前内容
     */
    private String beforeContent;
    /**
     * 主体内容
     */
    private List<ContentJson> contentJsons;
    /**
     * 主体之后内容
     */
    private String afterContent;

    /**
     * 消息分类
     */
    private String classification;
    /**
     * 就诊时间
     */
    private String visitTime;
    /**
     * 健康之路-预约挂号订单号
     */
    private String orderId;
    /**
     * 我的就诊-是否通知：0为通知，1为不通知。我的档案：0未评价、1为已评价
     */
    private String notifieFlag;
    /**
     * 总部推送消息类型 ，101：挂号结果推送，102：退号结果推送，-101：订单操作推送，100：满意度调查
     */
    private String portalMessagerTemplateType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeforeContent() {
        return beforeContent;
    }

    public void setBeforeContent(String beforeContent) {
        this.beforeContent = beforeContent;
    }


    public String getAfterContent() {
        return afterContent;
    }

    public void setAfterContent(String afterContent) {
        this.afterContent = afterContent;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }

    public List<ContentJson> getContentJsons() {
        return contentJsons;
    }

    public void setContentJsons(List<ContentJson> contentJsons) {
        this.contentJsons = contentJsons;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNotifieFlag() {
        return notifieFlag;
    }

    public void setNotifieFlag(String notifieFlag) {
        this.notifieFlag = notifieFlag;
    }

    public String getPortalMessagerTemplateType() {
        return portalMessagerTemplateType;
    }

    public void setPortalMessagerTemplateType(String portalMessagerTemplateType) {
        this.portalMessagerTemplateType = portalMessagerTemplateType;
    }

    public static class ContentJson {

        /**
         * code : orgCode
         * name : 机构代码
         * value : 49229004X
         */
        private String code;
        private String name;
        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
