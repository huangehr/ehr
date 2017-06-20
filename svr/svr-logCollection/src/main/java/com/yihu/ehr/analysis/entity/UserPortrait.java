package com.yihu.ehr.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户画像实体类
 *
 * Created by lyr-pc on 2017/2/20.
 */
@Entity
@Table(name = "wlyy_user_portrait")
public class UserPortrait {
    // 主键ID
    private Long id;
    // 居民code
    private String userCode;
    // 画像类别
    private String category;
    // 画像子类别
    private String subCategory;
    // 标签值
    private String value;
    // 创建日期
    private Date czrq;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    public Date getCzrq() {
        return czrq;
    }

    public void setCzrq(Date czrq) {
        this.czrq = czrq;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
