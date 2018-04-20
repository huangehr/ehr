package com.yihu.ehr.basic.portal.model;


import javax.persistence.*;

/**
 * 问卷调查题库选项表
 * Created by zhangdan on 2018/4/17.
 */
@Entity
@Table(name = "portal_survey_questions_options")
public class SurveyQuestionOption{
    private static final long serialVersionUID = 1L;
    private Long id;
    private String code;
    private Integer haveComment;//是否有选项说明（0没有 1有）
    private String questionCode;//问题编码
    private String content;//选项内容
    private Integer isRequired;//选项说明是否必填（0否 1是）
    private Integer sort;//单题内排序
    private String del;//删除标志（1正常，0删除）

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

    @Column(name = "have_comment")
    public Integer getHaveComment() {
        return haveComment;
    }

    public void setHaveComment(Integer haveComment) {
        this.haveComment = haveComment;
    }

    @Column(name = "question_code")
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

    @Column(name = "is_required")
    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }
}
