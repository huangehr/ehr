package com.yihu.ehr.user.service;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "user_privilege")
@Access(value = AccessType.PROPERTY)
public class UserPrivilege {
    private String id;         //id
    private String userId;     //用户id
    private String privilege;  //权限
    private int adminUser;   //特定管理员用户

    public UserPrivilege() {
    }

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
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "privilege",  nullable = true)
    public String getPrivilege() {
        return privilege;
    }
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    @Column(name = "admin_user",  nullable = true)
    public int getAdminUser() {
        return adminUser;
    }
    public void setAdminUser(int adminUser) {
        this.adminUser = adminUser;
    }
}