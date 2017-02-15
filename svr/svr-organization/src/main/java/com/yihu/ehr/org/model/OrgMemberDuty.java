package com.yihu.ehr.org.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  用户-职务关联表
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@Entity
@Table(name = "org_duty", schema = "", catalog = "healtharchive")
public class OrgMemberDuty {
    private int id;
    private int dutyId;
    private String userId;

    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "duty_id", nullable = false, insertable = true, updatable = true)
    public int getDutyId() {
        return dutyId;
    }

    public void setDutyId(int dutyId) {
        this.dutyId = dutyId;
    }

    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
