package com.yihu.ehr.entity.api;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Entity - Api错误码说明
 * Created by progr1mmer on 2018/3/14.
 */
@Entity
@Table(name = "app_api_error_code")
@Access(value = AccessType.PROPERTY)
public class AppApiErrorCode {

    private Integer id; //id
    private Integer appApiId; //apiId
    private Integer code; //错误码
    private String description; //描述
    private String solve; //解决方案
    private Integer sort; //序号

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "identity")
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "app_api_id", nullable = false)
    public Integer getAppApiId() {
        return appApiId;
    }

    public void setAppApiId(Integer appApiId) {
        this.appApiId = appApiId;
    }

    @Column(name = "code", nullable = false)
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "solve", nullable = false)
    public String getSolve() {
        return solve;
    }

    public void setSolve(String solve) {
        this.solve = solve;
    }

    @Column(name = "sort", nullable = false)
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
