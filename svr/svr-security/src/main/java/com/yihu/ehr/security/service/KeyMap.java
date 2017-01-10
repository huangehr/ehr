package com.yihu.ehr.security.service;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.id.ObjectId;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 平台安全对象与公-私钥之间的关联关系。
 *
 * @author cws
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Entity
@Table(name = "user_key")
@Access(value = AccessType.PROPERTY)
public class KeyMap implements Serializable{
    @Value("${admin-region}")
    short adminRegion;

    public KeyMap() {
        id = new ObjectId(adminRegion, BizObject.User).toString();
    }

    private String id;
    private String user;
    private String org;
    private String keyType;
    private String key;

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

    @Column(name = "user_id",  nullable = true)
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    @Column(name = "org_code",  nullable = true)
    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = org;
    }

    @Column(name = "key_type",  nullable = true)
    public String getKeyType() {
        return keyType;
    }
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    @Column(name = "key_id",  nullable = true)
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}