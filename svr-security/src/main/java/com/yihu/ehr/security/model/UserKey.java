package com.yihu.ehr.security.model;

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
public class UserKey implements Serializable{

    public UserKey() {
        id  = UUID.randomUUID().toString();
    }

    private String id;
    private String user;  //用户
    private String org;  //个人 或者 机构
    private String keyType;  //
    private String key;   //userSecurity


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

    @Column(name = "user-id",  nullable = true)
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    @Column(name = "org-code",  nullable = true)
    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = org;
    }

    @Column(name = "key-type",  nullable = true)
    public String getKeyType() {
        return keyType;
    }
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    @Column(name = "key-id",  nullable = true)
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}