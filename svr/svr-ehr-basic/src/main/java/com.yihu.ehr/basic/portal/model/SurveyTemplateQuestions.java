package com.yihu.ehr.basic.portal.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 问卷调查模板题目表
 * Created by zhangdan on 2018/4/14.
 */
@Entity
@Table(name = "portal_survey_template_questions", schema = "", catalog = "healtharchive")
public class SurveyTemplateQuestions {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String code;
    private String templateCode;//模板code
    private String title;//标题
    private String comment;//问题说明
    private Integer questionType;//问题类型（0单选 1多选 2问答）
    private Integer isRequired;//是否必答（0否 1是）
    private Long minNum;//最小答案个数（多选有效）
    private Long maxNum;//最大答案个数（多选有效）
    private String del;//删除标志（1正常，0删除）
    private String questionCodeNext;//下一题问题编码（问答题逻辑跳转）
    private String questionSortNext;
    private Long sort;//排序 分为模板内排序、问卷内排序、题库可不排序
    private Date createTime;              //创建时间
    private Date updateTime;//修改时间

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "question_comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(name = "question_type")
    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    @Column(name = "is_required")
    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    @Column(name = "min_num")
    public Long getMinNum() {
        return minNum;
    }

    public void setMinNum(Long minNum) {
        this.minNum = minNum;
    }

    @Column(name = "max_num")
    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    @Column(name = "template_code")
    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    @Column(name = "question_code_next")
    public String getQuestionCodeNext() {
        return questionCodeNext;
    }

    public void setQuestionCodeNext(String questionCodeNext) {
        this.questionCodeNext = questionCodeNext;
    }

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    public String getQuestionSortNext() {
        return questionSortNext;
    }

    public void setQuestionSortNext(String questionSortNext) {
        this.questionSortNext = questionSortNext;
    }
}
