package com.yihu.ha.security.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * 用于关联用户表及用户公私钥信息表.
 *
 * @author cws
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
@Entity
@Table(name = "user-key")
@Access(value = AccessType.PROPERTY)
public class UserKey  {

    public UserKey() {
        id  = UUID.randomUUID().toString();
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private String id;    //

    @Column(name = "user-id",  nullable = true)
    private String userId;  //用户

    @Column(name = "org-code",  nullable = true)
    private String orgCode;  //个人 或者 机构

    @Column(name = "key-type",  nullable = true)
    private String keyType;  //

    @Column(name = "key-id",  nullable = true)
    private String keyId;   //userSecurity



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}