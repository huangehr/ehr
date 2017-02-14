package com.yihu.ehr.user.entity;

import javax.persistence.*;

/**
 * Created by cws on 2017/2/6.
 */
@Entity
@Table(name = "user_truename_auth")
@Access(value = AccessType.PROPERTY)
public class UserTrueNameAuth {


    private long id;

    @Column(name = "user_id",nullable = true)
    private String userId;

    @Column(name = "id_card_no",nullable = true)
    private String idCardNo;

    @Column(name = "name",nullable = true)
    private String name;

    @Column(name = "auth_time",nullable = true)
    private String authTime;

    @Column(name = "auth_result",nullable = true)
    private String authResult;

    public UserTrueNameAuth(){}


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
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

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
    }
}
