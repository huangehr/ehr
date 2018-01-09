package com.yihu.ehr.basic.user.entity;

import javax.persistence.*;

/**
 * Created by yww on 2016/7/7.
 */
@Entity
@Table(name = "role_feature_relation")
@Access(value = AccessType.PROPERTY)
public class RoleFeatureRelation {
    private long id;
    private long roleId;
    private long featureId;

    public RoleFeatureRelation() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "role_id",nullable = false)
    public long getRoleId() {
        return roleId;
    }
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Column(name = "feature_id",nullable = false)
    public long getFeatureId() {
        return featureId;
    }
    public void setFeatureId(long featureId) {
        this.featureId = featureId;
    }
}
