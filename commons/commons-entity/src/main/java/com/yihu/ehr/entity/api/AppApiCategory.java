package com.yihu.ehr.entity.api;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;

/**
 * Entity - Api业务类别
 * Created by progr1mmer on 2018/3/14.
 */
@Entity
@Table(name = "app_api_category")
@Access(value = AccessType.PROPERTY)
public class AppApiCategory extends BaseIdentityEntity {

    private String name;
    private String description;

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
