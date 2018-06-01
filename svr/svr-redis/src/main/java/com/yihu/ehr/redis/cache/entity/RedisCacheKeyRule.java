package com.yihu.ehr.redis.cache.entity;

import com.yihu.ehr.entity.BaseIdentityEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 缓存Key生成规则 entity
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@Entity
@Table(name = "redis_cache_key_rule")
public class RedisCacheKeyRule extends BaseIdentityEntity {

    private String name; // 规则名称
    private String code; // 规则编码
    private String categoryCode; // 缓存分类编码
    private String expression; // 规则表达式
    private String simpleExpression; // 精简规则表达式
    private String expireTime; // 过期时间，单位秒
    private String remark; // 备注

    // 临时字段
    private String categoryName; // 缓存分类名称

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "category_code", nullable = false)
    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Column(name = "expression", nullable = false)
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Column(name = "simple_expression", nullable = false)
    public String getSimpleExpression() {
        return simpleExpression;
    }

    public void setSimpleExpression(String simpleExpression) {
        this.simpleExpression = simpleExpression;
    }

    @Column(name = "expire_time")
    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Formula("( SELECT c.name FROM redis_cache_category c WHERE c.code = category_code )")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
