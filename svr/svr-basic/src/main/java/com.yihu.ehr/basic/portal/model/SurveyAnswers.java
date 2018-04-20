package com.yihu.ehr.basic.portal.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhangdan on 2018/04/14.
 */
@Entity
@Table(name = "portal_survey_answers",schema = "",catalog = "healtharchive")
public class SurveyAnswers implements Serializable {
    private Long id;

    //问答题答案编码
    private String code;

    //问卷编码
    private String surveyCode;

    //问答题题目编码
    private String questionCode;

    //问答题答案
    private String content;

    //创建时间
    private Date createTime;

    //用户表id
    private String userId;

    public SurveyAnswers() {
    }

    public SurveyAnswers(String code, String surveyCode, String questionCode, String content, Date createTime) {
        this.code = code;
        this.surveyCode = surveyCode;
        this.questionCode = questionCode;
        this.content = content;
        this.createTime = createTime;
    }

    public SurveyAnswers(String code, String surveyCode, String questionCode, String content, Date createTime, String userId) {
        this.code = code;
        this.surveyCode = surveyCode;
        this.questionCode = questionCode;
        this.content = content;
        this.createTime = createTime;
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSurveyCode() {
        return surveyCode;
    }

    public void setSurveyCode(String surveyCode) {
        this.surveyCode = surveyCode;
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
