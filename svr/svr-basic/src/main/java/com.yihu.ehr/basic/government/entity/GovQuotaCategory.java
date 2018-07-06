package com.yihu.ehr.basic.government.entity;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 政府服务平台首页指标分类 Entity
 *
 * @author 张进军
 * @date 2018/7/5 17:34
 */
@Entity
@Table(name = "gov_quota_category")
public class GovQuotaCategory extends BaseIdentityEntity {

    public String code; // 首页指标分类编码
    public String name; // 首页指标分类名称

    @Column(name = "code", nullable = false, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
