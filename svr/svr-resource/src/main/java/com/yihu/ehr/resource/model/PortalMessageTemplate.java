package com.yihu.ehr.resource.model;

import javax.persistence.*;

/**
 * @author zdm
 * @since 2018.04.21
 */
@Entity
@Table(name = "portal_message_template", schema = "", catalog = "healtharchive")
public class PortalMessageTemplate {

    private Long id;

    /**
     * 消息分类
     */
    private String classification;

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
    private String content;
    /**
     * 主体之后内容
     */
    private String afterContent;
    /**
     * 部推送类型 ，101：挂号结果推送，102：退号结果推送，-101：订单操作推送
     */
    private String type;
    /**
     * 模板状态：0生效，1失效
     */
    private String state;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAfterContent() {
        return afterContent;
    }

    public void setAfterContent(String afterContent) {
        this.afterContent = afterContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
