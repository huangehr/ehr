package com.yihu.ehr.redis.cache.entity;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 缓存授权 entity
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@Entity
@Table(name = "redis_cache_authorization")
public class RedisCacheAuthorization extends BaseIdentityEntity {

    private String categoryCode; // 缓存分类编码
    private String appId; // 应用ID
    private String authorizedCode; // 授权码
    private String remark; // 备注

    @Column(name = "category_code", nullable = false)
    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Column(name = "app_id", nullable = false)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "authorized_code", nullable = false)
    public String getAuthorizedCode() {
        return authorizedCode;
    }

    public void setAuthorizedCode(String authorizedCode) {
        this.authorizedCode = authorizedCode;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
