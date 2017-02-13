package com.yihu.ehr.user.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by cws on 2017/2/6.
 */
@Entity
@Table(name = "user_login_log")
@Access(value = AccessType.PROPERTY)
public class userLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    private long id;

    @Column(name = "user_id",nullable = true)
    private String userId;

    @Column(name = "login_time",nullable = true)
    private Date loginTime;

    @Column(name = "last_login_time",nullable = true)
    private Date lastLoginTime;

    @Column(name = "ip",nullable = true)
    private String ip;

    public userLoginLog(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
