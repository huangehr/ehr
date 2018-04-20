package com.yihu.ehr.basic.portal.model;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zhangdan  on 2018/4/14.
 */
@Entity
@Table(name = "portal_survey_statistics",schema = "",catalog = "healtharchive")
public class SurveyStatistics implements Serializable{

    private Long id;
//    业务主键
    private String code;
//    问卷编码
    private String surveyCode;
//    题目编码
    private String questionCode;
//    题目名称
    private  String questionTitle;
//    选项编码
    private String optionsCode;
//    人数（加1）
   private Integer amount;
//   选项内容可为null
    private  String content;
//    是否有选项说明（0没有 1有）可为null
    private  Integer haveComment;
//    问题类型（0单选 1多选 2问答）
    private Integer type;
//    问题排序
    private  Integer qstSort;
//    选项排序可为null
    private  Integer optSort;


    public SurveyStatistics() {
    }

    public SurveyStatistics(String code, String surveyCode, String questionCode, String questionTitle, String optionsCode, Integer amount, String content, Integer haveComment, Integer type, Integer qstSort, Integer optSort) {
        this.code = code;
        this.surveyCode = surveyCode;
        this.questionCode = questionCode;
        this.questionTitle = questionTitle;
        this.optionsCode = optionsCode;
        this.amount = amount;
        this.content = content;
        this.haveComment = haveComment;
        this.type = type;
        this.qstSort = qstSort;
        this.optSort = optSort;
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

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getOptionsCode() {
        return optionsCode;
    }

    public void setOptionsCode(String optionsCode) {
        this.optionsCode = optionsCode;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getHaveComment() {
        return haveComment;
    }

    public void setHaveComment(Integer haveComment) {
        this.haveComment = haveComment;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getQstSort() {
        return qstSort;
    }

    public void setQstSort(Integer qstSort) {
        this.qstSort = qstSort;
    }

    public Integer getOptSort() {
        return optSort;
    }

    public void setOptSort(Integer optSort) {
        this.optSort = optSort;
    }
}
