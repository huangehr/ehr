package com.yihu.ehr.redis.cache.entity;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 缓存分类 entity
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@Entity
@Table(name = "redis_cache_category")
public class RedisCacheCategory extends BaseIdentityEntity {

    private String name; // 缓存分类名称
    private String code; // 缓存分类编码
    private String remark; // 备注

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "code", nullable = false, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
