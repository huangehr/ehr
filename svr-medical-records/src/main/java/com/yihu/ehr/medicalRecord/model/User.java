package com.yihu.ehr.medicalRecord.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Guo Yanshan on 2016/7/12.
 * 用户信息
 */
@Entity
@Table(name = "users")
@Access(value = AccessType.PROPERTY)
public class User {
    private String id;
    private String loginCode;
    private String realName;
    private String email;
    private String userType;
    private String organization;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "organization", unique = true, nullable = false)
    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Column(name = "user_type", unique = true, nullable = false)
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "login_code", unique = true, nullable = false)
    public String getLoginCode() {
        return loginCode;
    }
    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    @Column(name = "real_name", unique = true, nullable = false)
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
}
