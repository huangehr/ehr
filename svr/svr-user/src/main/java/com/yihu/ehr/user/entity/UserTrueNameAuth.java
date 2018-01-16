package com.yihu.ehr.user.entity;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "user_truename_auth", schema = "", catalog = "healtharchive")
public class UserTrueNameAuth {
    private int id;
    private String userId;
    private String idCardNo;
    private String name;
    private String authTime;
    private String authResult;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = false, insertable = true, updatable = true, length = 32)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "id_card_no", nullable = false, insertable = true, updatable = true, length = 32)
    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "auth_time", nullable = false, insertable = true, updatable = true, length = 32)
    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    @Basic
    @Column(name = "auth_result", nullable = false, insertable = true, updatable = true, length = 10)
    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTrueNameAuth that = (UserTrueNameAuth) o;

        if (id != that.id) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (idCardNo != null ? !idCardNo.equals(that.idCardNo) : that.idCardNo != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (authTime != null ? !authTime.equals(that.authTime) : that.authTime != null) return false;
        if (authResult != null ? !authResult.equals(that.authResult) : that.authResult != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (idCardNo != null ? idCardNo.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (authTime != null ? authTime.hashCode() : 0);
        result = 31 * result + (authResult != null ? authResult.hashCode() : 0);
        return result;
    }
}