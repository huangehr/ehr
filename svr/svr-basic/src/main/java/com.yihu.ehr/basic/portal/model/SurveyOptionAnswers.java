package com.yihu.ehr.basic.portal.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhangdan on 2018/4/14.
 */
@Entity
@Table(name = "portal_survey_option_answers",schema = "",catalog = "healtharchive")
public class SurveyOptionAnswers implements Serializable {
    private Long id;

    //选择答案编码
    private String code;

    //问卷编码
    private String surveyCode;

    //选择题题目编码
    private String questionCode;

    //选项编码
    private String optionsCode;

    //选项说明（可为null）
    private String optionComment;

    //答案类型（0单选 1多选）
    private  int answerType;

    //创建时间
    private Date createTime;

    //用户表id
    private String userId;

    public SurveyOptionAnswers() {
    }

    public SurveyOptionAnswers(String code, String surveyCode, String questionCode, String optionsCode, String optionComment, int answerType, Date createTime) {
        this.code = code;
        this.surveyCode = surveyCode;
        this.questionCode = questionCode;
        this.optionsCode = optionsCode;
        this.optionComment = optionComment;
        this.answerType = answerType;
        this.createTime = createTime;
    }

    public SurveyOptionAnswers(String code, String surveyCode, String questionCode, String optionsCode, String optionComment, int answerType, Date createTime, String userId) {
        this.code = code;
        this.surveyCode = surveyCode;
        this.questionCode = questionCode;
        this.optionsCode = optionsCode;
        this.optionComment = optionComment;
        this.answerType = answerType;
        this.createTime = createTime;
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
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

    public String getOptionsCode() {
        return optionsCode;
    }

    public void setOptionsCode(String optionsCode) {
        this.optionsCode = optionsCode;
    }

    public String getOptionComment() {
        return optionComment;
    }

    public void setOptionComment(String optionComment) {
        this.optionComment = optionComment;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
