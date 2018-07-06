package com.yihu.ehr.basic.government.entity;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 政府服务平台首页指标展示管理 Entity
 *
 * @author 张进军
 * @date 2018/7/5 17:34
 */
@Entity
@Table(name = "gov_quota_visibility")
public class GovQuotaVisibility extends BaseIdentityEntity {

    public String type; // 首页指标类型编码
    public String code; // 首页指标编码
    public String name; // 首页指标名称
    public String isShow; // 是否展示，1：显示，0：不显示
    public String remark; // 备注

    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    @Column(name = "is_show", nullable = false)
    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
