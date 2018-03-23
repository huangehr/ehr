package com.yihu.ehr.basic.portal.model;

import javax.persistence.*;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
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
}
