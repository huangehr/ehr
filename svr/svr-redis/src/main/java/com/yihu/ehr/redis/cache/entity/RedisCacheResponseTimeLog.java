package com.yihu.ehr.redis.cache.entity;

import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 缓存获取的响应时间日志 entity
 *
 * @author 张进军
 * @date 2017/12/4 08:56
 */
@Entity
@Table(name = "redis_cache_response_time_log")
public class RedisCacheResponseTimeLog extends BaseAssignedEntity {

    private String cacheKey; // 缓存Key
    private String categoryCode; // 缓存分类编码
    private long responseTime; // 响应时长，单位毫秒

    @Column(name = "cache_key", nullable = false)
    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    @Column(name = "category_code", nullable = false)
    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Column(name = "response_time", nullable = false)
    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
