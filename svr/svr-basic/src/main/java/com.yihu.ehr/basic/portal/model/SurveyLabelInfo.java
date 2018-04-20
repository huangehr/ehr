package com.yihu.ehr.basic.portal.model;


import javax.persistence.*;

/**
 * 问卷调查标签信息表
 * Created by zhangdan on 2018/4/14.
 */
@Entity
@Table(name = "portal_survey_label_info",schema = "",catalog = "healtharchive")
public class SurveyLabelInfo {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String code;
    private String relationCode;//关联编码
    private Integer useType;//关联编码类型(0模板 1问卷)
    private Integer label;//标签(1满意度、2糖尿病、3高血压、4生活日常)

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

    @Column(name = "relation_code")
    public String getRelationCode() {
        return relationCode;
    }

    public void setRelationCode(String relationCode) {
        this.relationCode = relationCode;
    }

    @Column(name = "use_type")
    public Integer getUseType() {
        return useType;
    }

    public void setUseType(Integer useType) {
        this.useType = useType;
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }
}
