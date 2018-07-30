package com.yihu.ehr.basic.user.entity;

import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_type")
@Access(value = AccessType.PROPERTY)
public class UserType extends BaseAssignedEntity {

    private String code;
    private String name;
    private String activeFlag;
    private String memo;

    public UserType() {
    }

    @Column(name = "code",  nullable = true)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name",  nullable = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "active_flag",  nullable = true)
    public String getActiveFlag() {
        return activeFlag;
    }
    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    @Column(name = "memo",  nullable = true)
    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }
}