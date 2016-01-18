package com.yihu.ehr.security.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by wq on 2015/8/3.
 */

@Entity
@Table(name = "user_token")
@Access(value = AccessType.PROPERTY)
public class UserToken implements Serializable {

    private String tokenId;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private Date createDate;
    private Date updateDate;
    private String userId;
    private String appId;

    public UserToken() {
        tokenId  = UUID.randomUUID().toString();
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "token_id", unique = true, nullable = false)
    public String getTokenId() {
        return tokenId;
    }
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Column(name = "access_token",  nullable = true)
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Column(name = "refresh_token",  nullable = true)
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Column(name = "expires_in",  nullable = true)
    public int getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Column(name = "create_date",  nullable = true)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "update_date",  nullable = true)
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "user_id",  nullable = true)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "app_id",  nullable = true)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
