package com.yihu.ehr.basic.user.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "user_login_log", schema = "", catalog = "healtharchive")
public class UserLoginLog {
    private int id;
    private String userId;
    private Timestamp loginTime;
    private String lastLoginTime;
    private String ip;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "login_time", nullable = false, insertable = true, updatable = true)
    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    @Basic
    @Column(name = "last_login_time", nullable = false, insertable = true, updatable = true)
    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Basic
    @Column(name = "ip", nullable = true, insertable = true, updatable = true)
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserLoginLog that = (UserLoginLog) o;

        if (id != that.id) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (loginTime != null ? !loginTime.equals(that.loginTime) : that.loginTime != null) return false;
        if (lastLoginTime != null ? !lastLoginTime.equals(that.lastLoginTime) : that.lastLoginTime != null)
            return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (loginTime != null ? loginTime.hashCode() : 0);
        result = 31 * result + (lastLoginTime != null ? lastLoginTime.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        return result;
    }
}
